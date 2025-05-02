$(document).ready(function() {

    $(document).on('click', '.open-edit-modal', function() {
        let id = $(this).data('id')
        let name = $(this).data('name')
        let email = $(this).data('email')

        $('#editUserId').val(id);
        $('#editName').val(name);
        $('#editEmail').val(email);

    });

    // Делегирование события click для кнопок удаления
    $(document).on('click', '.open-delete-modal', function() {
        let id = $(this).data('id')
        let name = $(this).data('name')
        let email = $(this).data('email')
        let roles = $(this).data('roles')

        $('#deleteUserId').val(id);
        $('#deleteName').val(name);
        $('#deleteEmail').val(email);
        $('#deleteRoles').val(roles);
    });

});