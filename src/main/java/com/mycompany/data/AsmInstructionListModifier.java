package com.mycompany.data;

import java.util.List;
import java.util.Map;

public interface AsmInstructionListModifier<T extends Register> {

    public void modify(List<AsmLine<T>> asmLines, Map<String, Section> sectionMap);

}
