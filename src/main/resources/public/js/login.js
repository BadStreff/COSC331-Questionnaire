$(document).ready(function(){
    $('button.btn.btn-primary.login').click( function (e){
         $.post('/login', {'username': $("input[id='username']").val(),
                           'password': $("input[id='password']").val()}).done(function(result){
            console.log("Post data sent: ");
            console.log({'username': $("input[id='username']").val(),
                         'password': $("input[id='password']").val()});
            window.location.replace("http://localhost:4567/");
         });
    });
});