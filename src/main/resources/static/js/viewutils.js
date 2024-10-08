// TODO: We can remove this function when the HTMX migration is complete.
function disableButtonAndSubmitForm(button) {
    button.disabled = true;
    button.form.submit();
}
