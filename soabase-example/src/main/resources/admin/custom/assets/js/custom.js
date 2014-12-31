$(function(){
    var customCount = 0;
    $('#custom-button').click(function(){
        ++customCount;
        var str = (customCount == 1) ? 'time' : 'times';
        $('#custom-content').text('You clicked me ' + customCount + ' ' + str + '.');
    });
});