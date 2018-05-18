var ajaxUrl = 'rest/profile/meals/';
var datatableApi;

$(document).ready(function () {
    $("#filterForm").submit(filter);
    $("#addButton").on("click", add);
    $("#mealForm").submit(save);
    $("#filterForm").on("reset", function (e) {
        updateTable(null);
    });
    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(event, jqXHR, options, jsExc);
    });
    datatableApi = $('#mealsTable').DataTable({
        paging: true,
        searching: false,
        info: true,
        columns: [
            {
                data: "dateTime"
            },
            {
                data: "description"
            },
            {
                data: "calories"
            },
            {
                defaultContent: "Edit",
                orderable: false
            },
            {
                defaultContent: "Delete",
                orderable: false
            }

        ],

        order: [
            [
                0,
                "desc"
            ]
        ]
    });
    makeEditable();
});

function add() {
    $("#mealModal").modal();
}

function isFilterExist() {
    var formElements = document.forms["filterForm"].elements;
    var counter = 0;
    $.each(formElements, function (key, value) {
        counter = value.value.toString() === "" ? counter : ++counter;
    });
    return counter !== 0;
}

function makeEditable() {
    $('.delete').click(function () {
        deleteRow($(this).parents("tr").attr("id"));
    });
}

function deleteRow(id) {
    $.ajax({
        url: ajaxUrl + id,
        type: 'DELETE',
        success: function () {
            var isFilterExist = isFilterExist();
            if (isFilterExist) {
                var form = $("#filterForm");
                form.trigger("submit");
            }
            else {
                updateTable()
            }
        }
    });
}

function updateTable(filterData) {
    if (filterData) {
        populateTable(filterData);
    }
    else {
        $.get(ajaxUrl, function (data) {
            populateTable(data);
        });

    }
}

function populateTable(data) {
    datatableApi.clear();
    $.each(data, function (key, item) {
        datatableApi.row.add(item);
    });
    datatableApi.draw();
}

function save(e) {
    e.preventDefault();
    var form = $("#mealForm");
    $.ajax({
            url: ajaxUrl,
            type: "POST",
            processDate: false,
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify(objectifyForm(form.serializeArray())),
            success: function () {
                if (isFilterExist()) {
                    var form = $("#filterForm");
                    form.trigger("submit");
                }
                else {
                    updateTable()
                }
                $("#mealModal").modal("hide");
            }
        }
    )
}

function objectifyForm(formArray) {//serialize data function

    var returnArray = {};
    for (var i = 0; i < formArray.length; i++) {
        returnArray[formArray[i]['name']] = formArray[i]['value'];
    }
    return returnArray;
}

function filter(e) {
    e.preventDefault();
    if (isFilterExist()) {
        var form = $("#filterForm");
        $.ajax({
            url: ajaxUrl + "filter",
            type: "GET",
            dataType: "json",
            data: form.serialize(),
            success: function (data) {
                updateTable(data);
                successNoty("Meals were filtered")
            }

        })
    }
    else {
        noty({
            text: "Filter doesn't exist",
            type: 'info',
            layout: 'bottomRight',
            timeout: 1500
        });
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

var failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}
