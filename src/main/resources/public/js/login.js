$(document).ready(function(){

    console.log('jQuery successfully loaded!');

    var r = 'None';

    $('a.btn.btn-primary.login').click( function (e){
        $.post('/login', {'username': $("input[id='username']").val(),
                          'password': $("input[id='password']").val()}).done(function(result){

                r = result;
                console.log("Response: " + result + "\n");
                console.log("Post data sent: ");
                console.log({'username': $("input[id='username']").val(),
                             'password': $("input[id='password']").val()});
                e.preventDefault();
        });
    });
});