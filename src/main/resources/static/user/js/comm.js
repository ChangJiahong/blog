/**
 * @author CJH
 * @date 2019/3/16
 */


document.oncopy = addLink;

function addLink() {
    var body_element = document.body;
    var selection;
    selection = window.getSelection();

    var locationHref=document.location.href;
    var appendLink="<br>------------------------------br>原文出自[ CJH的个人博客 ] 转载请保留原文链接: <a href='"+locationHref+"'>"+locationHref+"</a>";
    if (window.clipboardData) { // Internet Explorer
        var copytext = selection + appendLink;
        window.clipboardData.setData ("Text", copytext);
        return false;
    } else {
        var copytext = selection + appendLink;
        var newdiv = document.createElement('div');
        newdiv.style.position='absolute';
        newdiv.style.left='-99999px';
        body_element.appendChild(newdiv);
        newdiv.innerHTML = copytext;
        selection.selectAllChildren(newdiv);
        window.setTimeout(function() {
            body_element.removeChild(newdiv);
        },0);
    }
}