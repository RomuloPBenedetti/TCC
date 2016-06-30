Blockly.Blocks['click_single_special'] = {
  init: function() {
    this.appendDummyInput("imageInput")
        .appendField("clicar uma vez em:")
        .appendField(new Blockly.FieldImageButton("../images/icons/clickBlack.png", 30, 30, "", imageButtonEvent), "FieldImageButton");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(60);
    this.setTooltip('Procura na sua tela pela região que a imagem representa e clica no centro uma veze, para escolher, clique na imagem');
  }
};

Blockly.Blocks['click_doble_special'] = {
  init: function() {
    this.appendDummyInput("imageInput")
        .appendField("clicar duas vezes em:")
        .appendField(new Blockly.FieldImageButton("../images/icons/clickBlack.png", 30, 30, "", imageButtonEvent), "FieldImageButton");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(60);
    this.setTooltip('Procura na sua tela pela região que a imagem representa e clica no centro duas vezes, para escolher, clique na imagem');
    console.log(this.id);
  }
};

Blockly.Blocks['text_typer'] = {
  init: function() {
    this.appendDummyInput("textToType")
        .appendField("digitar:")
        .appendField(new Blockly.FieldTextInput("texto"), "text");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(60);
    this.setTooltip('');
    this.setHelpUrl('http://www.example.com/');
  }
};

Blockly.Blocks['Image_Wait'] = {
  init: function() {
    this.appendDummyInput("imageInput")
        .appendField("esperar enquanto não encontrar:")
        .appendField(new Blockly.FieldImageButton("../images/icons/clickBlack.png", 30, 30, "", imageButtonEvent), "FieldImageButton");
    this.appendValueInput("miliToWait")
        .setCheck("Number")
        .setAlign(Blockly.ALIGN_RIGHT)
        .appendField("procurando a cada:");
    this.appendDummyInput()
        .appendField("milisegundos");
    this.setInputsInline(true);
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(60);
    this.setTooltip('');
    this.setHelpUrl('http://www.example.com/');
  }
};

Blockly.JavaScript['click_doble_special'] = function(block) {
  src = block.getFieldValue('FieldImageButton');
  running = 'if (running) {\n';
  calling = '    running = java.clickIn(\"' + src + '\",' + 2 + ');\n';
  end =     '}\n';
  var code = running + calling + end;
  return code;
};

Blockly.JavaScript['click_single_special'] = function(block) {
  src = block.getFieldValue('FieldImageButton');
  running = 'if (running) {\n';
  calling = "java.clickIn(\"" + src + '\",' + 1 + ');\n';
  end =     '}\n';
  var code = running + calling + end;
  return code;
};

Blockly.JavaScript['text_typer'] = function(block) {
  text = block.getFieldValue('text');
  running = 'if (running) {\n';
  calling = "    running = java.type(\"" + text + '\");\n';
  end =     '}\n';
  var code = running + calling + end;
  return code;
};

Blockly.JavaScript['Image_Wait'] = function(block) {
  src = block.getFieldValue('FieldImageButton');
  milisec = Blockly.JavaScript.valueToCode(block, 'miliToWait', Blockly.JavaScript.ORDER_ATOMIC) || '0'
  running = 'if (running) {\n';
  calling = '    java.waitImg(\"' + src + '\", '+ milisec + ');\n';
  end =     '}\n';
  var code = running + calling + end;
  return code;
};