package com.mycompany.data;

import java.util.List;
import java.util.Map;

public interface AsmInstructionListModifier {

    public void modify(List<AsmLine<?>> asmLines, Map<String, Section> sectionMap/*, final Map<String, Long> labelMap*/);

}
