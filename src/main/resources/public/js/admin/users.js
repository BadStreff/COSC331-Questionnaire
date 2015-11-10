$(document).ready(function(){
    $('button#delete').click( function (e) {
        var id = $(this).parent().parent().attr('id');
         $.post('/admin/delete_user', {'username': id}).done(function(result){
                    window.location.href = '/admin/users';
                    console.log(result);
         });
    });
});