/*
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function soaShowInfiniteProgressBar() {
    $('#soa-progress-container').show();
}

function soaHideInfiniteProgressBar() {
    $('#soa-progress-container').hide();
}

function soaShowPage() {
    var h = location.hash;
    var command = '';
    if ( !h || (h === '#') ) {
        command = soaDefaultTabId;
        if ( history.pushState ) {
            history.pushState(null, null, '#' + soaDefaultTabId);
        }
    } else {
        command = h.slice(1);
    }
    command = 'soa-tab-' + command;

    for (var i in soaTabIds) {
        var id = soaTabIds[i];
        var item = $('#' + id);
        var liItem = $('#' + id + "-li");
        if ( id === command ) {
            item.show(function(){
                $(this).trigger('soa-show');
            });
            liItem.addClass('active');
        } else {
            item.hide(function(){
                $(this).trigger('soa-hide');
            });
            liItem.removeClass('active');
        }
    }
}

$(function() {
    soaAutoLoadTemplates();
    soaShowPage();
    $(window).on('hashchange', function() {
        soaShowPage();
    });
});