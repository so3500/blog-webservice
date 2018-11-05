$(document).ready(function () {
    convertToMarkdown();
});

var converter = new showdown.Converter();
function convertToMarkdown() {
    $(".table-content").each(function (index) {
        var mdContent = converter.makeHtml($(this).text());
        $(this).html(mdContent);
    })
}