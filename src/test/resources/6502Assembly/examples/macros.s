loop:
    MyMacro()
    JMP loop

.macro MyMacro()
{ 
    INC $D020
}