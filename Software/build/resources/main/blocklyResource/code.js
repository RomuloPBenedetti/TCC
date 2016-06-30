
var onresize
function load(xmlDoc){
    var blocklyArea = document.getElementById('blocklyArea');
    var blocklyDiv = document.getElementById('blocklyDiv');

    var workspace = Blockly.inject(blocklyDiv,
        {
            grid: {spacing: 25, length: 3, colour: '#ccc', snap: true},
            media: 'core/media/',
            toolbox: xmlDoc,
            zoom: {controls: true, wheel: true}
        }
    );

    onresize = function(e) {
        var element = blocklyArea;
        var x = 0; var y = 0;

        do {
            x += element.offsetLeft;
            y += element.offsetTop;
            element = element.offsetParent;
        } while (element);

        blocklyDiv.style.left = x + 'px';
        blocklyDiv.style.top = y + 'px';
        blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
        blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
        Blockly.svgResize(workspace);
    };
    window.addEventListener('onresize', onresize, true);
    onresize();
}

function imageButtonEvent(){
    alert(this.sourceBlock_.id);
    java.StartclickCapture(this.sourceBlock_.id);
}

function setImageButtonByID (id, width, height, src){
    alert(id);
    alert(src);
    alert(width +" "+ height);
    width = Number(width);
    height = Number(width);
    mainworkspace = Blockly.mainWorkspace;
    block = mainworkspace.getBlockById(id);
    input = block.getInput("imageInput");
    input.removeField("FieldImageButton");
    input.appendField(new Blockly.FieldImageButton(src, width, height, "", imageButtonEvent), "FieldImageButton")
}

function runAndGetCode (){
    mainworkspace = Blockly.mainWorkspace;
    code = Blockly.JavaScript.workspaceToCode(mainworkspace);
    code = "var running = true;\ " + code;
    eval(code);
    return code;
}


function saveBlocks () {
    var xml = Blockly.Xml.workspaceToDom(Blockly.mainWorkspace);
    var xml_text = Blockly.Xml.domToText(xml);
    return xml_text;
}

function loadBlocks (xmlText) {
    xml = Blockly.Xml.textToDom(xmlText);
    Blockly.mainWorkspace.clear();
    Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xml);
}

window.onerror = function(msg, url, linenumber) {
    alert('Error message: '+msg+'\nURL: '+url+'\nLine Number: '+linenumber);
    return true;
}
