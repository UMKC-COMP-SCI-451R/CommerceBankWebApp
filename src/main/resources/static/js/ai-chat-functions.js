let conversation = [];
function getConversation(){
    var conversationDiv = document.getElementById('conversationDiv');
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/getConversation', true); // Change to GET and the endpoint to '/fetch'

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var data = JSON.parse(xhr.responseText); // data is a list of string
            console.log(data)
            if(data.length!==0){
                document.getElementById("closeChatBtn").style.display='block';
                conversationDiv.style.display = 'block';
                if(!conversationDiv.style.height){
                    setTimeout(() => {
                        conversationDiv.style.height='350px';
                    }, 10);
                }
                data.forEach(function(e,i){
                    conversation.push(e)
                    var div;
                    if(i % 2 === 0){
                        div = appendMessageDiv(e,"user");
                    }else div = appendMessageDiv(e,"assistant");
                    conversationDiv.appendChild(div);
                    setTimeout(() => {
                        div.style.opacity = '1';
                    }, 10);
                });
                conversationDiv.scrollTop = conversationDiv.scrollHeight;
            }
        } else if (xhr.readyState === 4) {
            // Handle error here
            console.error(xhr.statusText);
        }
    };

    xhr.send();
}
function toggleQAdiv(){
    var QAdiv = document.getElementById('QAdiv')
    var QAButtonContainer = document.getElementById('QAButtonContainer')
    if(QAdiv.style.display !== 'block'){
        QAdiv.style.display='block';
        setTimeout(() => {
            QAdiv.style.opacity = '1';
        }, 10);
        QAButtonContainer.style.visibility='hidden';
        if(conversation.length === 0){
            getConversation();
        }
    }
    else{
        QAdiv.style.opacity = '0';
        setTimeout(() => {
            QAdiv.style.display = 'none';
        }, 500);
        QAButtonContainer.style.visibility='visible';
    }
}


document.getElementById('question').addEventListener('keydown', function(event) {
    // Check if Enter is pressed without the Shift key
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault(); // Prevent the default behavior (inserting a newline)
        writeAndSubmit(); // Submit the form
    }
});

function writeAndSubmit(){
    var newMessage = document.getElementById('question').value
    var conversationDiv = document.getElementById('conversationDiv');
    conversation.push(newMessage);
    conversationDiv.style.display = 'block';
    document.getElementById('loadingGif').style.display ='block';
    setTimeout(() => {
        document.getElementById('loadingGif').style.opacity = '1';
    }, 10);
    document.getElementById('question').value = "";
    const div = appendMessageDiv(newMessage,"user");
    if(!conversationDiv.style.height){
        setTimeout(() => {
            conversationDiv.style.height='350px';
        }, 10);
        document.getElementById("closeChatBtn").style.display='block';
    }
    conversationDiv.appendChild(div);
    setTimeout(() => {
        div.style.opacity = '1';
    }, 10);

    conversationDiv.scrollTop = conversationDiv.scrollHeight;

    askQuestion(conversation);
}

function askQuestion(messages) {
    var index = 0;
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/ask', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var response = xhr.responseText;
            conversation.push(response);
            const div = appendMessageDiv("","assistant")
            document.getElementById('conversationDiv').appendChild(div);
            setTimeout(() => {
                div.style.opacity = '1';
            }, 10);
            appendLetterByLetter(div.getElementsByClassName("messageDiv").item(0),response,index);
        }

    };
    xhr.send(JSON.stringify(messages));
}

function appendMessageDiv(newMessage, role){
    var conversationDiv = document.getElementById('conversationDiv');
    const div = document.createElement("div");
    div.classList.add('messageContainerDiv');
    const messageDiv = document.createElement("div");
    const messageLogoDiv = document.createElement("div");
    messageDiv.classList.add('messageDiv')
    messageDiv.textContent = newMessage;
    const img = document.createElement("img");
    img.style.width = "30px"; // Example width
    img.style.height = "30px"; // Example height
    messageLogoDiv.classList.add('messageLogoDiv')
    if(role == "user"){
        img.src = "images/user-icon.png";
        messageLogoDiv.appendChild(img)
        div.appendChild(messageLogoDiv);
        div.appendChild(messageDiv);
    }else{
        img.src = "images/open-ai-logo.png";
        messageLogoDiv.appendChild(img)
        div.appendChild(messageDiv);
        div.appendChild(messageLogoDiv);
    }

    div.style.marginBottom = "10px";
    return div
}


function appendLetterByLetter(messageDiv,response, index){
    if (index < response.length) {
        messageDiv.textContent += response.charAt(index); // Append current letter
        document.getElementById('conversationDiv').scrollTop = document.getElementById('conversationDiv').scrollHeight;
        setTimeout(() => appendLetterByLetter(messageDiv,response, index + 1), 10); // Wait 10ms then append next letter
    }else{
        setTimeout(() => {
            document.getElementById('loadingGif').style.opacity = 0;
        }, 500);
        setTimeout(() => {
            document.getElementById('loadingGif').style.display ='none';
        }, 1000);
    }
}

function showEndingMessage(){
    document.getElementById("closingMessage").style.display = 'block'
    setTimeout(() => {
        document.getElementById('closingMessage').style.opacity = 1;
    }, 10);
}

function cancelEndChat(){
    document.getElementById('closingMessage').style.opacity = 0;
    setTimeout(() => {
        document.getElementById("closingMessage").style.display = 'none'
    }, 10);
}

function endChat(){
    var thankYouMessage = document.getElementById("thankYouMessage");
    var conversationDiv = document.getElementById('conversationDiv');
    var ratingMessage = document.getElementById("ratingMessage");
    ratingMessage.style.display ='none'
    ratingMessage.style.opacity = 0;
    resetRating();
    thankYouMessage.style.display = 'block'
    setTimeout(() => {
        thankYouMessage.style.opacity = 1;
    }, 10);
    setTimeout(() => {
        toggleQAdiv();
        thankYouMessage.style.display = 'none'
        thankYouMessage.style.opacity = 0;
        conversationDiv.style.display = 'none';
        conversationDiv.style.height='';
        document.getElementById("closeChatBtn").style.display='none';
        conversationDiv.innerHTML='';
    }, 3000);
    conversation = []
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/closeConversation', true); // Change to GET and the endpoint to '/fetch'
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var data = xhr.responseText; // data is a list of string
            console.log(data)
        } else if (xhr.readyState === 4) {
            // Handle error here
            console.error(xhr.statusText);
        }
    }
    xhr.send();

}

function showRatingMessage(){
    var closingMessage =  document.getElementById("closingMessage");
    closingMessage.style.display = 'none'
    closingMessage.style.opacity = 0;
    document.getElementById("ratingMessage").style.display = 'block';
    setTimeout(() => {
        document.getElementById("ratingMessage").style.opacity = 1;
    }, 10);
}

function rate(num){
    resetRating();
    for (let i = 1; i <= num; i++) {
        var star = document.getElementById("star"+i);
        if(star.classList.contains("fa-star-o"))
            star.classList.remove("fa-star-o");
        if(!star.classList.contains("fa-star"))
            star.classList.add("fa-star");
    }
}

function resetRating(){
    for (let i = 1; i <= 5; i++) {
        document.getElementById("star"+i).classList.add("fa-star-o");
        document.getElementById("star"+i).classList.remove("fa-star");
    }
}