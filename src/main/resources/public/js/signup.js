$(document).ready(function(){
    $('#username').focusout(function (e) {
        $.post('/userexist', {'username': $("input[id='username']").val(),
                                   'password': $("input[id='password']").val()}).done(function(result){
                    console.log("Post data sent: ");
                    console.log({'username': $("input[id='username']").val(),
                                 'password': $("input[id='password']").val()});
                    console.log(result);
                 });
        $('.glyphicon-user').css('text-shadow','0 0 2px #5cb85c');
        $('.glyphicon-user').css('color','#5cb85c');
    });

    $('#email').focusout(function (e) {
        $('.glyphicon-envelope').css('text-shadow','0 0 2px #5cb85c');
        $('.glyphicon-envelope').css('color','#5cb85c');
    });

    $('#password').focusout(function (e) {
        if($('#password').val() == $('#confirmpassword').val()) {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #5cb85c');
            $('.glyphicon-asterisk').css('color','#5cb85c');
        }
        else {
            $('.glyphicon-asterisk').css('text-shadow','0 0 2px #d9534f');
            $('.glyphicon-asterisk').css('color','#d9534f');
        }
    });
    $('#confirmpassword').focusout(function (e) {
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
                    case "success":
                        window.location.href = '/';
                        break;
                    case "failure":
                        console.log("signup failed");
                        break;
                }
        });
    });
});
/*
@brand-primary: darken(#428bca, 6.5%); // #337ab7
@brand-success: #5cb85c;
@brand-info:    #5bc0de;
@brand-warning: #f0ad4e;
@brand-danger:  #d9534f;
*/