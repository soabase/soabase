var customCount = 0;

function customUpdate() {
    var str = (customCount == 1) ? 'time' : 'times';
    $('#custom-content').text('You clicked me ' + customCount + ' ' + str + '.');
}

$(function(){
    $('#custom-button').click(function(){
        ++customCount;
        customUpdate();
    });
    customUpdate();
});