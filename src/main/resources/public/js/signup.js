$(document).ready(function(){

    console.log('jQuery successfully loaded!');

    var r = 'None';

    $('a.btn.btn-primary.signup').click( function (e){
        $.post('/sign_up', {'username': $("input[id='username']").val(),
                            'email': $("input[id='email']").val(),
                            'password': $("input[id='password']").val(),
                            'confirmpassword': $("input[id='confirmpassword']").val()}).done(function(result){

                r = result;
                console.log("Response: " + result + "\n");
                console.log("Post data sent: ");
                console.log({'username': $("input[id='username']").val(),
                             'email': $("input[id='email']").val(),
                             'password': $("input[id='password']").val(),
                             'confirmpassword': $("input[id='confirmpassword']").val()});
                e.preventDefault();
        });
    });
});