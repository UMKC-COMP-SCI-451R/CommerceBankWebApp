function toggleQAdiv(){
    var QAdiv = document.getElementById('QAdiv')
    var QAButtonContainer = document.getElementById('QAButtonContainer')
    if(QAdiv.style.display !== 'block'){
        QAdiv.style.display='block';
        setTimeout(() => {
            QAdiv.style.opacity = '1';
        }, 10);
        QAButtonContainer.style.visibility='hidden';
    }
    else{
        QAdiv.style.opacity = '0';
        setTimeout(() => {
            QAdiv.style.display = 'none';
        }, 1000);
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
    document.getElementById('question').value = "";
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
    img.src = "images/user-icon.png";
    messageLogoDiv.appendChild(img)
    div.appendChild(messageLogoDiv);
    div.appendChild(messageDiv);
    div.style.marginBottom = "10px";
    if(!conversationDiv.style.height){
        setTimeout(() => {
            conversationDiv.style.height='350px';
        }, 10);
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
            const div = document.createElement("div");
            const messageDiv = document.createElement("div");
            const messageLogoDiv = document.createElement("div");
            div.classList.add('messageContainerDiv');
            messageDiv.classList.add('messageDiv')
            const img = document.createElement("img");
            img.style.width = "30px"; // Example width
            img.style.height = "30px"; // Example height
            messageLogoDiv.classList.add('messageLogoDiv')
            img.src = "images/open-ai-logo.png";
            messageLogoDiv.appendChild(img)
            div.appendChild(messageDiv);
            div.appendChild(messageLogoDiv);
            div.style.marginBottom = "10px";
            document.getElementById('conversationDiv').appendChild(div);
            setTimeout(() => {
                div.style.opacity = '1';
            }, 10);
            appendLetterByLetter(messageDiv,response,index);
        }
        document.getElementById('loadingGif').style.display ='none';
    };
    console.log(messages)
    xhr.send(JSON.stringify(messages));
}


function appendLetterByLetter(messageDiv,response, index){
    if (index < response.length) {
        messageDiv.textContent += response.charAt(index); // Append current letter
        document.getElementById('conversationDiv').scrollTop = document.getElementById('conversationDiv').scrollHeight;
        setTimeout(() => appendLetterByLetter(messageDiv,response, index + 1), 10); // Wait 10ms then append next letter
    }
}