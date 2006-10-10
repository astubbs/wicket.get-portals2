var WicketAjaxDebug = {

	showResponseText : false,

	debugWindowId : "wicketAjaxDebugWindow",
	
	debugWindowDragHandleId : "wicketAjaxDebugWindowHandle",
	
	debugWindowLogId : "wicketAjaxDebugWindowLogId",
	
	getDebugWindow : function() {
		WicketAjaxDebug.init();
	    wicketGet(WicketAjaxDebug.debugWindowId);
	},
	
	showDebugWindow : function() {
		WicketAjaxDebug.init();
	    wicketShow(WicketAjaxDebug.debugWindowId);
	},
	
	hideDebugWindow : function() {
		WicketAjaxDebug.init();
	    wicketHide(WicketAjaxDebug.debugWindowId);
	},

    log : function(msg, label) {
		WicketAjaxDebug.init();
        var d = wicketGet(WicketAjaxDebug.debugWindowLogId);
        var c = document.createElement("div");
		
        msg = msg.replace(/</g, "&lt;");
        msg = msg.replace(/>/g, "&gt;");
        msg = msg.replace(/\n/g, "<br/>");
        msg = msg.replace(/ /g, "&nbsp;");  
        msg = msg.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
		
		if (typeof(label) != "undefined")
			msg = "<b>" + label + "</b>" + msg;
		          
        c.innerHTML = msg;
        c.setAttribute("style","font-size: 82%; margin: 0px; padding:0px");        
        d.appendChild(c);
        d.scrollTop = d.scrollHeight;
    },
    
    logError : function(msg) {
		WicketAjaxDebug.init();
        WicketAjaxDebug.log(msg, "ERROR: ");
    },

    logInfo : function(msg) {
		WicketAjaxDebug.init();
        WicketAjaxDebug.log(msg, "INFO: ");
    },

    clearLog : function() {
		WicketAjaxDebug.init();
        var d = wicketGet(WicketAjaxDebug.debugWindowLogId);
		d.innerHTML = "";
    },

	init : function() {		

        if ( wicketAjaxDebugEnabled()) {
        	var wad=WicketAjaxDebug;
        	var dwid=wad.debugWindowId;
        	var dwdhid=wad.debugWindowDragHandleId;

			var firstTime = document.getElementById(dwid) == null;

			if (firstTime) {
				
	            var html = 	        	
					"<div style='width: 50em; display: none; position: absolute; left: 200px; top: 300px; z-index: 1000;' id='"+dwid+"'>"+
					"	<div style='border: 1px solid black; padding: 1px; background-color: #eee'>"+
					"		<div style='overflow: auto; width: 100%'>"+
					"			<div style='float: right; padding: 0.2em; padding-right: 1em;'>"+
					"				<a href='javascript:WicketAjaxDebug.clearLog()' style='color:blue'>clear</a> | "+
					"				<a href='javascript:WicketAjaxDebug.hideDebugWindow()' style='color:blue'>close</a>"+
					"			</div>"+
					"			<div id='"+dwdhid+"' style='padding: 0.2em; background-color: gray; color: white; padding-left: 1em; margin-right: 8em; cursor: move;'>"+
					"				Wicket Ajax Debug Window (drag me here)"+
					"			</div>"+
					"			<div id='"+WicketAjaxDebug.debugWindowLogId+"' style='width: 100%; height: 30em; background-color: white; overflow: auto; white-space: nowrap'>"+
					"			</div>"+
					"		</div>"+					
					"	</div>" +
					"</div>"+
					"<div style='position:absolute; left:10px; top:95%; z-index:100;'>"+
					"    <a href='javascript:WicketAjaxDebug.showDebugWindow()'>WICKET AJAX DEBUG</a>"+
					"</div>";
												
				
				WicketAjaxDebug.addElement(html);
            	WicketDrag.init(wicketGet(dwdhid), wicketGet(dwid));
			}

        }
	},
	
	addElement : function(html) {
		var element = document.createElement("div");				
		element.innerHTML = html;
		document.body.appendChild(element);
	},

	addEvent: function(obj, evType, fn) { 
		if (obj.addEventListener) { 
			obj.addEventListener(evType, fn, false); 
			return true; 
		} else if (obj.attachEvent) { 
			var r = obj.attachEvent("on"+evType, fn); 
   			return r; 
		} else { 
   			return false; 
		} 
	}
};

WicketAjaxDebug.addEvent(window, "load", WicketAjaxDebug.init);