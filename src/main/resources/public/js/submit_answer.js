$(document).ready(function(){
    $('button.btn.btn-success.submit').click( function (e){
        var aid = $('input[name=answerRadio]:checked', '.radio').val();
        $.post('/submit_answer', {'choice_id': aid}).done(function(result){
           window.location.href = '/';
           console.log(result);
        });
    });
});