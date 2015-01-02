var soaTemplates = {};

function soaEscapeRegExp(string){
    return string.replace(/([.*+?^${}()|\[\]\/\\])/g, "\\$1");
}

function soaAutoLoadTemplates() {
    $('.soa-template').each(function(item){
        soaLoadTemplate(this.id);
    });
}

function soaLoadTemplate(id) {
    var item = $('#' + id);
    var template = item.html();
    item.remove();
    soaTemplates[id] = template;
}

function soaGetTemplate(id, replacements) {
    var template = soaTemplates[id];
    if ( replacements ) {
        $.each(replacements, function(key, value) {
            var pattern = new RegExp(soaEscapeRegExp(key), 'g');
            template = template.replace(pattern, value);
        });
    }
    return template;
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
