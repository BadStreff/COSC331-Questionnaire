$(document).ready(function(){
    var choice_id = 0;
    $('button.add_choice').click( function (e){
        choice_id++;
         $(".choices").append("<div id=\""+ choice_id +"\"><form class=\"form-inline margin-top-10\"><div class=\"form-group\"><input type=\"text\" class=\"form-control\" placeholder=\"choice here\" id=\"choice\"></div><button class=\"btn btn-warning\" id=\"remove_choice\"><span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span></button></form></div>");
    });

    $('.choices').on( "click", "button#remove_choice", function() {
        id = $(this).parent().parent().attr('id');
        //console.log("Removing choice id: " + id);
        $("#" + id.toString()).remove();
        return false;   //jquery trickery to not reload the page
    });

    $('button#create_question').click( function (e){
        //grab all the choices
        //var arr = [];
        var r = "";
        $( "input#choice" ).each(function( index ) {
            //arr.push($( this ).val());
            r += $( this ).val() + "###";
        });
        console.log(r);
        $.post('/create_question', {'question_text': $("#questionText").val(),
                          'question_choices': r}).done(function(result){
            console.log("Post data sent");
            window.location.href = '/admin/questions';
         });
    });
});
