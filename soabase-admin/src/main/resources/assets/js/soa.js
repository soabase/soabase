function soaShowPage() {
    var h = location.hash;
    var command = '';
    if ( !h || (h === '#') ) {
        command = 'soa-tab-';
    } else {
        command = 'soa-tab-' + h.slice(1);
    }

    for (var i in soaTabIds) {
        var id = soaTabIds[i];
        var item = $('#' + id);
        var liItem = $('#' + id + "-li");
        if ( id === command ) {
            item.show();
            liItem.addClass('active');
        } else {
            item.hide();
            liItem.removeClass('active');
        }
    }
}

$(function() {
    soaShowPage();
    $(window).on('hashchange', function() {
        soaShowPage();
    });
});