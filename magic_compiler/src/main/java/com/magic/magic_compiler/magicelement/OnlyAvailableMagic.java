package com.magic.magic_compiler.magicelement;

import com.magic.magic_annotations.OnlyAvailable;

import javax.lang.model.element.Element;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 20:22
 * Description:
 */
public class OnlyAvailableMagic extends MagicElement {

    private final String typeName;

    public OnlyAvailableMagic(Element element) {
        super(element);
        typeName = element.getAnnotation(OnlyAvailable.class).value();
    }


    public String getTypeName() {
        return typeName;
    }
}
