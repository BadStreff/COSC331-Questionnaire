$(document).ready(function(){
    $('button.btn.btn-primary.login').click( function (e){
         $.post('/login', {'username': $("input[id='username']").val(),
                           'password': $("input[id='password']").val()}).done(function(result){
            console.log("Post data sent: ");
            console.log({'username': $("input[id='username']").val(),
                         'password': $("input[id='password']").val()});
            switch(result) {
                case "success":
                    window.location.href = '/';
                    break;
                case "failure":
                    console.log("login failed");
                    $("#error_modal").modal('show');
                    break;
            }
            console.log(result);
         });
    });
});