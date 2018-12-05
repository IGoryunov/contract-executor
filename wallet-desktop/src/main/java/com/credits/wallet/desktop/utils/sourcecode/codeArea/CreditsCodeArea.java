package com.credits.wallet.desktop.utils.sourcecode.codeArea;

import com.credits.wallet.desktop.AppState;
import com.credits.wallet.desktop.utils.sourcecode.codeArea.autocomplete.AutocompleteHelper;
import com.credits.wallet.desktop.utils.sourcecode.codeArea.autocomplete.CreditsProposalsPopup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

public class CreditsCodeArea extends CodeArea {


    private static int tabCount;

    private static final String DEFAULT_SOURCE_CODE =
        "public class Contract extends SmartContract {\n" + "\n" + "    public Contract() {\n\n    }" + "\n" + "}";

    public static final String SPACE_SYMBOL = " ";
    public static final String CURLY_BRACKET_SYMBOL = "{";
    public static final String ROUND_BRACKET_SYMBOL = "(";
    public static final String NEW_LINE_SYMBOL = "\n";


    public CreditsToolboxPopup popup;
    public AutocompleteHelper autocompleteHelper;
    public CreditsProposalsPopup creditsProposalsPopup;

    public CreditsCodeArea(boolean readOnly, double prefHeight, double prefWidth) {
        super();
        this.setPrefHeight(prefHeight);
        this.setPrefWidth(prefWidth);
        this.initCodeAreaLogic();
        popup = new CreditsToolboxPopup(this, readOnly);
        creditsProposalsPopup = new CreditsProposalsPopup();
        autocompleteHelper = new AutocompleteHelper(this, creditsProposalsPopup);
    }

    public void initCodeAreaLogic() {

        this.sceneProperty().addListener((observable, old, newPropertyValue) -> {
            if (newPropertyValue == null) {
                this.cleanAll();
            }
        });

        this.addEventHandler(KeyEvent.KEY_PRESSED, (k) -> {
            KeyCode code = k.getCode();
            if (code != KeyCode.TAB) {
                if (code.isLetterKey() || code.isDigitKey() || code.isNavigationKey() || code.isWhitespaceKey()) {
                    tabCount = 0;
                }
            }
            this.autocompleteHelper.handleKeyPressEvent(k);
        });

        Nodes.addInputMap(this, InputMap.consume(keyPressed(KeyCode.TAB), e -> {
            tabCount++;
            this.replaceSelection("    ");
        }));

        Nodes.addInputMap(this, InputMap.consume(keyPressed(KeyCode.BACK_SPACE), e -> {
            if (tabCount > 0) {
                for (int i = 0; i < 4; i++) {
                    this.deletePreviousChar();
                }
                tabCount--;
            } else {
                this.deletePreviousChar();
            }
        }));

        if (AppState.lastSmartContract == null) {
            this.replaceText(0, 0, DEFAULT_SOURCE_CODE);
        } else {
            this.replaceText(AppState.lastSmartContract);
        }
    }

    public void doAutoComplete(String textToInsert) {
        String token = "%";
        String text = this.getText().replace(token, "?");
        int caretPos = this.getCaretPosition();
        int lastIndexOfSpaceSymbol = text.lastIndexOf(SPACE_SYMBOL, caretPos - 1);
        int lastIndexOfCurlyBracketSymbol = text.lastIndexOf(CURLY_BRACKET_SYMBOL, caretPos - 1);
        int lastIndexOfRoundBracketSymbol = text.lastIndexOf(ROUND_BRACKET_SYMBOL, caretPos - 1);
        int lastIndexOfNewLineSymbol = text.lastIndexOf(NEW_LINE_SYMBOL, caretPos - 1);
        StringBuilder b = new StringBuilder(text);
        if (lastIndexOfSpaceSymbol != -1) {
            b.replace(lastIndexOfSpaceSymbol, lastIndexOfSpaceSymbol + 1, token);
        }
        if (lastIndexOfCurlyBracketSymbol != -1) {
            b.replace(lastIndexOfCurlyBracketSymbol, lastIndexOfCurlyBracketSymbol + 1, token);
        }
        if (lastIndexOfRoundBracketSymbol != -1) {
            b.replace(lastIndexOfRoundBracketSymbol, lastIndexOfRoundBracketSymbol + 1, token);
        }
        if (lastIndexOfNewLineSymbol != -1) {
            b.replace(lastIndexOfNewLineSymbol, lastIndexOfNewLineSymbol + 1, token);
        }
        String textReplacedSymbols = b.toString();
        int spacePos = textReplacedSymbols.lastIndexOf(token, caretPos);
        this.replaceText(spacePos + 1, caretPos, textToInsert);
    }

    public void positionCursorToLine(int line) {
        char[] text = this.getText().toCharArray();
        int pos = 0;
        int curLine = 1;
        while (pos < text.length) {
            if (line <= curLine) {
                break;
            }
            if (text[pos] == '\n') {
                curLine++;
            }
            pos++;
        }
        this.displaceCaret(pos);
        this.showParagraphAtTop(Math.max(0, line - 5));
        this.requestFocus();
    }

    public void cleanAll() {
        popup.hide();
        creditsProposalsPopup.clear();
        creditsProposalsPopup.hide();
        if (AppState.executor != null) {
            AppState.executor.shutdown();
        }
    }

}