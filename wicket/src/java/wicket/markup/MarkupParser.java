/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.util.io.Streams;
import wicket.util.parse.metapattern.parsers.VariableAssignmentParser;
import wicket.util.parse.metapattern.parsers.WordParser;
import wicket.util.resource.Resource;
import wicket.util.resource.ResourceNotFoundException;
import wicket.util.string.StringValue;


/**
 * A fairly shallow markup parser. Parses a markup string of a given type of markup (for
 * example, html, xml, vxml or wml) into Tag and RawMarkup tokens. The Tag tokens must
 * have the attributeName attribute that was passed to the scanner's constructor. Only
 * tags having an attribute with the same name as the attributeName with which this markup
 * scanner was constructed are returned. Text before, between and after such tags are
 * returned as String values. A check is done to ensure that tags returned balance
 * correctly.
 * @author Jonathan Locke
 */
public final class MarkupParser
{
    /** Code broadcaster for reporting. */
    private static final Log log = LogFactory.getLog(MarkupParser.class);

    /** Name of desired componentName tag attribute. */
    private final String componentNameAttribute;

    /** Position in parse. */
    private int inputPosition;

    /** Input to parse. */
    private String input;

    /** True to strip out HTML comments. */
    private boolean stripComments;

    /** True to compress multiple spaces/tabs or line endings to a single space or line ending. */
    private boolean compressWhitespace;

    /** Current line and column numbers during parse. */
    private int lineNumber = 1;

    /** current column number. */
    private int columnNumber = 1;

    /** Last place we counted lines from. */
    private int lastLineCountIndex;

    /** Null, if JVM default. Else from <?xml encoding=""> */
    private String encoding;

    /** Regex to find <?xml encoding ... ?> */
    private static final Pattern encodingPattern = Pattern.compile("<\\?xml\\s+(.*\\s)?encoding\\s*=\\s*([\"\'](.*?)[\"\']|(\\S]*)).*\\?>");
    
    /**
     * Constructor.
     * @param componentNameAttribute The name of the componentName attribute
     */
    public MarkupParser(final String componentNameAttribute)
    {
        this.componentNameAttribute = componentNameAttribute;
        //this.autoIndexPrefix = autoIndexPrefix;
    }

    /**
     * Return the encoding used while reading the markup file.
     * @return if null, than JVM default
     */
    public String getEncoding()
    {
        return encoding;
    }
    
    /**
     * Set whether to strip components.
     * @param stripComments whether to strip components.
     */
    public void setStripComments(boolean stripComments)
    {
        this.stripComments = stripComments;
    }

    /**
     * Set whether whitespace should be compressed.
     * @param compressWhitespace whether whitespace should be compressed.
     */
    public void setCompressWhitespace(boolean compressWhitespace)
    {
        this.compressWhitespace = compressWhitespace;
    }

    /**
     * Reads and parses markup from a file.
     * @param resource The file
     * @return The markup
     * @throws ParseException
     * @throws IOException
     * @throws ResourceNotFoundException
     */
    public Markup read(final Resource resource) throws ParseException, IOException,
            ResourceNotFoundException
    {
        // reset: Must come from markup
        this.encoding = null;
        
        try
        {
            // read-ahead the input stream, if it starts with <?xml encoding=".." ?>.
            // If yes, set this.encoding and return the character which follow it.
            // If not, return the whole line. determineEncoding will read-ahead
            // at max. the very first line of the markup
            final String pushBack = determineEncoding(resource.getInputStream());

            // Depending the encoding determine from the markup-file, read
            // the rest either with specific encoding or JVM default
            final String markup;
            if (encoding == null)
            {
                // Use JVM default to read the markup
                markup = pushBack + Streams.readString(resource.getInputStream());
            }
            else
            {
                // Use the encoding as specific in <?xml encoding=".." ?>
                markup = Streams.readString(resource.getInputStream(), encoding);
            }

            return new Markup(resource, parseMarkup(markup));
        }
        finally
        {
            resource.close();
        }
    }

    /**
     * Read-ahead the input stream (markup file). If it starts with 
     * &lt;?xml encoding=".." ?&gt;, than set this.encoding and return null. 
     * If not, return all characters read so far. determineEncoding 
     * will read-ahead at max. the very first line of the markup.
     * 
     * @param in The markup file
     * @return null, if &lt;?xml ..?&gt; has been found; else all characters read ahead
     * @throws IOException
     */
    final private String determineEncoding(final InputStream in) throws IOException
    {
        // max one line
        StringBuffer pushBack = new StringBuffer(80);
        
        int value;
        while ((value = in.read()) != -1)
        {
            pushBack.append((char) value);
            
            // stop at end of the first tag or end of line. If it is html without
            // newlines, stop after X bytes (= characters)
            if ((value == '>') 
                    || (value == '\n') 
                    || (value == '\r') 
                    || (pushBack.length() > 75))
            {
                break;
            }
        }
        
        // Does the string match the <?xml .. ?> pattern
        final Matcher matcher = encodingPattern.matcher(pushBack);
        if (!matcher.find())
        {
            // No; return the whole string
            return pushBack.toString();
        }
        
        // Extract the encoding
        encoding = matcher.group(3);
        if ((encoding == null) || (encoding.length() == 0))
        {
            encoding = matcher.group(4);
        }
        
        // Because we are removing <?xml ..?> from markup and because
        // there are no additional characters in pushBack, ...
        return null;
    }
    
    /**
     * Parse the markup.
     * @param string The markup
     * @return The markup
     * @throws ParseException
     */
    Markup parse(final String string) throws ParseException
    {
        return new Markup(null, parseMarkup(string));
    }

    /**
     * Scans the given markup string and extracts balancing tags.
     * @param markup The markup
     * @return An immutable list of immutable MarkupElement elements
     * @throws ParseException Thrown if markup is malformed or tags don't balance
     */
    private List parseMarkup(final String markup) throws ParseException
    {
        // List to return
        final List list = new ArrayList();

        // Create tag parser
        setInput(markup);

        // Tag stack to find balancing tags
        final Stack stack = new Stack();

        // Position in parse
        int position = 0;

        // Loop through tags
        for (ComponentTag tag; null != (tag = nextTag());)
        {
            if(log.isDebugEnabled())
            {
                log.debug("tag: " + tag.toUserDebugString() + ", stack: " + stack);
            }

            // True if tag should be added to list
            boolean addTag = false;

            // Check tag type
            if (tag.type == ComponentTag.OPEN)
            {
                // Push onto stack
                stack.push(tag);

                // We add open tags if they have the componentName attribute set
                addTag = tag.componentName != null;
            }
            else if (tag.type == ComponentTag.CLOSE)
            {
                // Check that there is something on the stack
                if (stack.size() > 0)
                {
                    // Pop the top tag off the stack
                    ComponentTag top = (ComponentTag) stack.pop();

                    // If the name of the current close tag does not match the
                    // tag on the stack
                    // then we may have a mismatched close tag
                    boolean mismatch = !top.getName().equalsIgnoreCase(tag.getName());

                    if (mismatch)
                    {
                        // Pop any simple tags off the top of the stack
                        while (mismatch && !top.requiresCloseTag())
                        {
                            // Pop simple tag
                            top = (ComponentTag) stack.pop();

                            // Does new top of stack mismatch too?
                            mismatch = !top.getName().equalsIgnoreCase(tag.getName());
                        }

                        // If adjusting for simple tags did not fix the problem,
                        // it must be a real mismatch.
                        if (mismatch)
                        {
                            throw new ParseException("Tag "
                                    + top.toUserDebugString() + " has a mismatched close tag at "
                                    + tag.toUserDebugString(), tag.getPos());
                        }
                    }

                    // Tag matches, so add pointer to matching tag
                    tag.closes = top;

                    // We want to add the tag if the open Tag on the stack had a
                    // componentName attribute
                    addTag = top.componentName != null;
                }
                else
                {
                    throw new ParseException("Tag "
                            + tag.toUserDebugString() + " does not have a matching open tag", tag
                            .getPos());
                }
            }
            else if (tag.type == ComponentTag.OPEN_CLOSE)
            {
                // Tag closes itself
                tag.closes = tag;

                // Does the open tag have the attribute we're looking for?
                addTag = tag.componentName != null;
            }

            // Add tag to list?
            if (addTag)
            {
                // Add text from last position to tag position
                if (tag.getPos() > position)
                {
                    String rawMarkup = markup.substring(position, tag.getPos());

                    if (stripComments)
                    {
                        rawMarkup = rawMarkup.replaceAll("<!--(.|\n|\r)*?-->", "");
                    }

                    if (compressWhitespace)
                    {
                        rawMarkup = rawMarkup.replaceAll("[ \\t]+", " ");
                        rawMarkup = rawMarkup.replaceAll("( ?[\\r\\n] ?)+", "\n");
                    }

                    list.add(new RawMarkup(rawMarkup));
                }

                // Add immutable tag
                tag.makeImmutable();
                list.add(tag);

                // Position is after tag
                position = tag.getPos() + tag.getLength();
            }
        }

        // If there's still a non-simple tag left, it's an error
        while (stack.size() > 0)
        {
            final ComponentTag top = (ComponentTag) stack.peek();

            if (!top.requiresCloseTag())
            {
                stack.pop();
            }
            else
            {
                throw new ParseException("Tag " + top + " at " + top.getPos()
                        + " did not have a close tag", top.getPos());
            }
        }

        // Add tail?
        if (position < markup.length())
        {
            list.add(new RawMarkup(markup.substring(position, markup.length())));
        }

        // Return immutable list after removing preview components marked by
        // "[remove]"
        removePreviewComponents(list);

        return Collections.unmodifiableList(list);
    }

    /**
     * Removes components marked as "[remove]" from list. Nested components are not
     * allowed, for obvious reasons.
     * @param list The list to process
     */
    private void removePreviewComponents(final List list)
    {
        // Remove any [remove] components
        for (int i = 0; i < list.size(); i++)
        {
            // Get next element
            final MarkupElement element = (MarkupElement) list.get(i);

            // If element is a component tag
            if (element instanceof ComponentTag)
            {
                // Check for open tag labelled "[remove]"
                final ComponentTag openTag = (ComponentTag) element;

                if (openTag.isOpen() && openTag.componentName.equalsIgnoreCase("[remove]"))
                {
                    // Remove open tag
                    list.remove(i);

                    // If a raw markup tag follows (new value at index i after
                    // deletion)
                    if ((i < list.size()) && list.get(i) instanceof RawMarkup)
                    {
                        // remove any raw markup
                        list.remove(i);
                    }

                    // Must have close tag
                    if ((i < list.size()) && list.get(i) instanceof ComponentTag)
                    {
                        // Get close tag
                        ComponentTag closeTag = (ComponentTag) list.get(i);

                        // Does it close the open tag?
                        if (closeTag.closes(openTag))
                        {
                            // Remove close tag
                            list.remove(i);

                            // Back up one because i++ is coming at the bottom
                            // of the loop
                            // and we still need to process list[i].
                            i--;
                        }
                        else
                        {
                            throw new MarkupException("[Remove] open tag "
                                    + openTag + " not closed by " + list.get(i));
                        }
                    }
                    else
                    {
                        throw new MarkupException("[Remove] open tag "
                                + openTag + " not closed by " + list.get(i));
                    }
                }
            }
        }
    }

    /**
     * Sets the input string to parse.
     * @param input The input string
     */
    private void setInput(final String input)
    {
        this.input = input;
        this.inputPosition = 0;
    }

    /**
     * Counts lines between indices.
     * @param string String
     * @param end End index
     */
    private void countLinesTo(final String string, final int end)
    {
        for (int i = lastLineCountIndex; i < end; i++)
        {
            if (string.charAt(i) == '\n')
            {
                columnNumber = 1;
                lineNumber++;
            }
            else
            {
                columnNumber++;
            }
        }

        lastLineCountIndex = end;
    }

    /**
     * Gets the next tag from the input string.
     * @return The extracted tag.
     * @throws ParseException
     */
    private ComponentTag nextTag() throws ParseException
    {
        // Index of open bracket
        int openBracketIndex = input.indexOf('<', this.inputPosition);

        // While we can find an open tag, parse the tag
        if (openBracketIndex != -1)
        {
            // Determine line number
            countLinesTo(input, openBracketIndex);

            // Get index of closing tag and advance past the tag
            final int closeBracketIndex = input.indexOf('>', openBracketIndex);

            if (closeBracketIndex == -1)
            {
                throw new ParseException("No matching close bracket at position "
                        + openBracketIndex, this.inputPosition);
            }

            // Get the tagtext between open and close brackets
            String tagText = input.substring(openBracketIndex + 1, closeBracketIndex);

            // Handle comments
            if (tagText.startsWith("!--"))
            {
                // Skip ahead to -->
                this.inputPosition = input.indexOf("-->", openBracketIndex + 4) + 3;

                if (this.inputPosition == -1)
                {
                    throw new ParseException(
                            "Unclosed comment beginning at " + openBracketIndex, openBracketIndex);
                }

                return nextTag();
            }
            else
            {
                // Type of tag
                ComponentTag.Type type = ComponentTag.OPEN;

                // If the tag ends in '/', it's a "simple" tag like <foo/>
                if (tagText.endsWith("/"))
                {
                    type = ComponentTag.OPEN_CLOSE;
                    tagText = tagText.substring(0, tagText.length() - 1);
                }
                else if (tagText.startsWith("/"))
                {
                    // The tag text starts with a '/', it's a simple close tag
                    type = ComponentTag.CLOSE;
                    tagText = tagText.substring(1);
                }

                // We don't deeply parse tags like DOCTYPE that start with !
                // or XML document definitions that start with ?
                if (tagText.startsWith("!") || tagText.startsWith("?") )
                {
                    // Move to position after the tag
                    this.inputPosition = closeBracketIndex + 1;

                    // Return next tag
                    return nextTag();
                }
                else
                {
                    // Parse remainting tag text, obtaining a tag object or null
                    // if it's invalid
                    final ComponentTag tag = parseTagText(tagText);

                    if (tag != null)
                    {
                        // Populate tag fields
                        tag.type = type;
                        tag.pos = openBracketIndex;
                        tag.length = (closeBracketIndex + 1) - openBracketIndex;
                        tag.getAttributes().makeImmutable();
                        tag.text = input.substring(openBracketIndex, closeBracketIndex + 1);
                        tag.lineNumber = lineNumber;
                        tag.columnNumber = columnNumber;

                        // Move to position after the tag
                        this.inputPosition = closeBracketIndex + 1;

                        // Return the tag we found!
                        return tag;
                    }
                    else
                    {
                        throw new ParseException(
                                "Malformed tag (line " + lineNumber + ", column "
                                + columnNumber + ")", openBracketIndex);
                    }
                }
            }
        }

        // There is no next matching tag
        return null;
    }

    /**
     * Parses the text between tags. For example, "a href=foo.html".
     * @param tagText The text between tags
     * @return A new Tag object or null if the tag is invalid
     */
    private ComponentTag parseTagText(final String tagText)
    {
        // Get the length of the tagtext
        final int tagTextLength = tagText.length();

        // If we match tagname pattern
        final WordParser tagnameParser = new WordParser(tagText);
        final ComponentTag tag = new ComponentTag();

        if (tagnameParser.matcher().lookingAt())
        {
            // Extract the tag from the pattern matcher
            tag.name = tagnameParser.getWord().toLowerCase();

            int pos = tagnameParser.matcher().end(0);

            // Are we at the end? Then there are no attributes, so we just
            // return the tag
            if (pos == tagTextLength)
            {
                return tag;
            }

            // Extract attributes
            final VariableAssignmentParser attributeParser = new VariableAssignmentParser(tagText);

            while (attributeParser.matcher().find(pos))
            {
                // Get key and value using attribute pattern
                String value = attributeParser.getValue();

                // Set new position to end of attribute
                pos = attributeParser.matcher().end(0);

                // Chop off double quotes
                if (value.startsWith("\""))
                {
                    value = value.substring(1, value.length() - 1);
                }

                // Trim trailing whitespace
                value = value.trim();

                // Get key
                final String key = attributeParser.getKey();

                // If the form <tag id = "wcn-value"> is used
                boolean wcnId = key.equalsIgnoreCase("id") && value.startsWith(componentNameAttribute + "-");

                if (wcnId)
                {
                    // extract component name from value
                    value = value.substring(componentNameAttribute.length() + 1).trim();
                }
                
                // If user-defined component name attribute is used OR the
                // standard name ("wcn") is used
                if (wcnId
                        || key.equalsIgnoreCase(componentNameAttribute)
                        || key.equalsIgnoreCase(ComponentTag.wicketComponentNameAttribute))
                {
                    // Set componentName value on tag
                    tag.componentName = value;
                }

                // Put the attribute in the attributes hash
                tag.attributes.put(key, StringValue.valueOf(value));

                // The input has to match exactly (no left over junk after
                // attributes)
                if (pos == tagTextLength)
                {
                    return tag;
                }
            }
        }

        return null;
    }
}

///////////////////////////////// End of File /////////////////////////////////
