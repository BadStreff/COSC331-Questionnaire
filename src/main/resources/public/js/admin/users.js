$(document).ready(function(){
    var id = "";
    $('button#delete').click( function (e) {
        id = $(this).parent().parent().attr('id');
         $.post('/admin/delete_user', {'username': id}).done(function(result){
                    window.location.href = '/admin/users';
                    console.log(result);
         });
    });
    $('button#reset').click( function (e) {
        id = $(this).parent().parent().attr('id');
        $("#reset_modal").modal('show');
    });

    $('button#confirm_reset').click( function (e) {
        $.post('/change_password', {'username': id,
                                    'password': $("input[id='new_password']").val()}).done(function(result){
            window.location.href = '/admin/users';
            console.log(result);
        });
        $("#reset_modal").modal('hide');
    });
});