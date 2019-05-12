package com.magic.magic_compiler.magicelement;


import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 20:07
 * Description:
 */
public class MagicElement {

    private final Element ELEMENT;
    private final String NAME;
    private final String QUALIFIEDNAME;

    public MagicElement(Element element) {
        this.ELEMENT = element;
        this.NAME = element.getSimpleName().toString();
        final TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        this.QUALIFIEDNAME = enclosingElement.getQualifiedName().toString();
    }

    public Element getElement() {
        return ELEMENT;
    }

    public String getName() {
        return NAME;
    }

    public String getQualifiedName() {
        return QUALIFIEDNAME;
    }
}
