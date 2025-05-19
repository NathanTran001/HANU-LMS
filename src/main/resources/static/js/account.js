$(document).ready(function () {
	$("#deleteConfirmModal").on("show.bs.modal", function (event) {
		var button = $(event.relatedTarget);
		var accountId = button.data("data-account-id");
		var deleteUrl = "/admin/delete/" + accountId;

		var modal = $(this);
		modal.find(".btn-danger").attr("href", deleteUrl);
	});
});
