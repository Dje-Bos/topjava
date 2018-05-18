function makeEditable() {
    $('.delete').click(function () {
        deleteRow($(this).parents("tr").attr("id"));
    });

    $('#detailsForm').submit(function () {
        save();
        return false;
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(event, jqXHR, options, jsExc);
    });

    $(":checkbox").each(function () {
        $(this).change(changeState);
    })
}

// function User(id, name, email, registered, enabled, roles) {
//     this.id = id;
//     this.name = name;
//     this.email = email;
//     this.registered = registered;
//     this.enabled = enabled;
//     this.roles = roles;
// }
function changeState(e) {
    var tr = $(this).parents("tr");
    var id = tr.attr("id");
    // var userData = datatableApi.row(tr).data();
    // var enabled = $(userData.enabled).is(":checked");
    // var name = userData.name;
    // var registered = Date.parse(userData.registered);
    // registered = new Date(registered);
    // var roles = userData.roles;
    // roles = roles.substring(1, roles.length-1);
    // roles = roles.split(", ");
    // var email = $(userData.email).text();
    //
    // var user = new User(id, name, email, registered, enabled, roles);
    // var jsonUser = JSON.stringify(user);
    $.ajax({
            url: ajaxUrl + id,
            type: "POST",
            success: function () {
                updateTable();
                successNoty('Updated');

            },
            error: function (jqXHR, status) {
                updateTable();

                // if (datatableApi.row(tr).data().enabled.is(":checked"))
                // datatableApi.row(tr).data().enabled.attr("checked");
                // else
                //     datatableApi.row(tr).data().enabled.removeAttr("checked");
            }
        }
    )
}

function add() {
    $('#id').val(null);
    $('#editRow').modal();
}

function deleteRow(id) {
    $.ajax({
        url: ajaxUrl + id,
        type: 'DELETE',
        success: function () {
            updateTable();
            successNoty('Deleted');
        }
    });
}

function updateTable() {
    $.get(ajaxUrl, function (data) {
        datatableApi.clear();
        $.each(data, function (key, item) {
            datatableApi.row.add(item);
        });
        datatableApi.draw();
    });
}

function save() {
    var form = $('#detailsForm');
    $.ajax({
        type: "POST",
        url: ajaxUrl,
        data: form.serialize(),
        success: function () {
            $('#editRow').modal('hide');
            updateTable();
            successNoty('Saved');
        }
    });
}

var failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    noty({
        text: text,
        type: 'success',
        layout: 'bottomRight',
        timeout: 1500
    });
}

function failNoty(event, jqXHR, options, jsExc) {
    closeNoty();
    failedNote = noty({
        text: 'Failed: ' + jqXHR.status + "<br>",
        type: 'error',
        layout: 'bottomRight'
    });
}
