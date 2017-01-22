//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

id("confirmusername").addEventListener("click", function () {
	sendNick(id("username").value);
	document.getElementById("usernamebox").style.display = "none";
	document.getElementById("confirmusername").style.display = "none";
	document.getElementById("newchan").style.visibility = "visible";
	document.getElementById("chname").style.visibility = "visible";
});

id("usernamebox").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
    	sendNick(id("username").value);
    	document.getElementById("usernamebox").style.display = "none";
    	document.getElementById("confirmusername").style.display = "none";
    	document.getElementById("newchan").style.visibility = "visible";
    	document.getElementById("chname").style.visibility = "visible";
    }
});

id("leavech").addEventListener("click", function() {
	document.getElementById("message").style.visibility = "hidden";
	document.getElementById("send").style.visibility = "hidden";
	document.getElementById("newchan").style.visibility = "visible";
	document.getElementById("chname").style.visibility = "visible";
	sendLeave();
});



id("newchan").addEventListener("click", function() {
	sendChannel(id("chname").value);
	document.getElementById("leavech").style.visibility = "visible";
});

id("chname").addEventListener("keypress", function (e) {
	if (e.keyCode === 13) {
		sendChannel(id("chname").value);
		document.getElementById("leavech").style.visibility = "visible";
	}
});

id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

function sendNick(nick)
{
	var obj= {
		username: nick
	};
	
	webSocket.send(JSON.stringify(obj));
}


//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
	
	var obj = {
		msg: message
	};
	
    if (message !== "") {
        webSocket.send(JSON.stringify(obj));
        id("message").value = "";
    }
}

function sendChannel(chan) {
	
	var obj = {
		ch: chan
	};
	
	if (chan !== "") {
		webSocket.send(JSON.stringify(obj));
		id("chname").value = "";
		document.getElementById("message").style.visibility = "visible";
		document.getElementById("send").style.visibility = "visible";
		document.getElementById("newchan").style.visibility = "hidden";
		document.getElementById("chname").style.visibility = "hidden";
		document.getElementById("leavech").style.visibility = "visible";
	}
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
	var data = JSON.parse(msg.data);
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function (user) {
       	insert("userlist", "<li>" + user + "</li>");
    });
    id("channellist").innerHTML = "";
    insert("channellist", "<button id=\"btn\">new</button>");
    data.chlist.forEach(function (channel) {
    	insert("channellist", "<li id=\""+ channel  +"\">" + channel + "</li>");
    });
    document.getElementById("channellist").addEventListener("click",function(e) {
        connectToChannel(e);
    });
}

function sendLeave() {
	var obj = {
		leave: 1
	};
	
	webSocket.send(JSON.stringify(obj));
	document.getElementById("leavech").style.visibility = "hidden";
}

function connectToChannel(e) {
	if(e.target.id == "btn") {
		document.getElementById("newchan").style.visibility = "visible";
    	document.getElementById("chname").style.visibility = "visible";
	}
	else {
		var obj = {
			channel: e.target.id
		};
	
		webSocket.send(JSON.stringify(obj));
		document.getElementById("message").style.visibility = "visible";
		document.getElementById("send").style.visibility = "visible";
		document.getElementById("newchan").style.visibility = "hidden";
		document.getElementById("chname").style.visibility = "hidden";
		document.getElementById("leavech").style.visibility = "visible";
	}
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}