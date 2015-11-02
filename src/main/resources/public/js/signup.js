$(document).ready(function(){
    var validUser=true;
    var username="";
    $('#username').focusout(function (e) {
        $.post('/userexist', {'username': $("input[id='username']").val(),
                                   'password': $("input[id='password']").val()}).done(function(result){
            console.log("Response: ");
            console.log(result);
            if(result == "false") {
                validUser=true;
                $('.glyphicon-user').css('text-shadow','0 0 2px #5cb85c');
                $('.glyphicon-user').css('color','#5cb85c');
            }
            else {
                validUser=false;
                $('.glyphicon-user').css('text-shadow','0 0 2px #d9534f');
                $('.glyphicon-user').css('color','#d9534f');
            }
        });
    });
    function checkUsername() {
        if(username == $("input[id='username']").val()) {
            return;
        }
        username = $("input[id='username']").val();
        $.post('/userexist', {'username': $("input[id='username']").val(),
                'password': $("input[id='password']").val()}).done(function(result){
            console.log("Response: ");
            console.log(result);
            if(result == "false") {
                validUser=true;
                $('.glyphicon-user').css('text-shadow','0 0 2px #5cb85c');
                $('.glyphicon-user').css('color','#5cb85c');
            }
            else {
                validUser=false;
                $('.glyphicon-user').css('text-shadow','0 0 2px #d9534f');
                $('.glyphicon-user').css('color','#d9534f');
            }
        });
    }
    setInterval(checkUsername, 1000);

    $('#email').keyup(function (e) {
        $('.glyphicon-envelope').css('text-shadow','0 0 2px #5cb85c');
        $('.glyphicon-envelope').css('color','#5cb85c');
    });

    $('#password').keyup(function (e) {
        if($('#password').val() == $('#confirmpassword').val()) {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #5cb85c');
            $('.glyphicon-asterisk').css('color','#5cb85c');
        }
        else {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #d9534f');
            $('.glyphicon-asterisk').css('color','#d9534f');
        }
    });
    $('#confirmpassword').keyup(function (e) {
        if($('#password').val() == $('#confirmpassword').val()) {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #5cb85c');
            $('.glyphicon-asterisk').css('color','#5cb85c');
        }
        else {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #d9534f');
            $('.glyphicon-asterisk').css('color','#d9534f');
        }
    });

    $('a.btn.btn-primary.signup').click( function (e){
        if(validUser && $('#password').val() == $('#confirmpassword').val()) {
            $.post('/sign_up', {'username': $("input[id='username']").val(),
                            'email': $("input[id='email']").val(),
                            'password': $("input[id='password']").val(),
                            'confirmpassword': $("input[id='confirmpassword']").val()}).done(function(result){

                console.log("Post data sent: ");
                console.log({'username': $("input[id='username']").val(),
                             'email': $("input[id='email']").val(),
                             'password': $("input[id='password']").val(),
                             'confirmpassword': $("input[id='confirmpassword']").val()});
                switch(result) {
                    case "true":
                        window.location.href = '/';
                        break;
                    case "false":
                        console.log("signup failed");
                        $("#error_modal").modal('show');
                        break;
                }
            });
        }
        else {
            $("#error_modal").modal('show');
        }
    });
});
/*
@brand-primary: darken(#428bca, 6.5%); // #337ab7
@brand-success: #5cb85c;
@brand-info:    #5bc0de;
@brand-warning: #f0ad4e;
@brand-danger:  #d9534f;
*/