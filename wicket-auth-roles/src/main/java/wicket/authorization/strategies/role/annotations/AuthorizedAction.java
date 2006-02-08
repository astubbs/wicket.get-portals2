/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.authorization.strategies.role.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A mapping of 1..n roles to an action. This annotions must be embedded in the
 * {@link wicket.authorization.strategies.role.annotations.AuthorizedActions} annotation.
 * 
 * @author Eelco Hillenius
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PACKAGE, ElementType.TYPE })
@Documented
@Inherited
public @interface AuthorizedAction {

	/**
	 * The action that is allowed.
	 * 
	 * @return the action that is allowed
	 */
	String action();

	/**
	 * The roles for this action.
	 * 
	 * @return the roles for this action
	 */
	String[] roles() default "";
}