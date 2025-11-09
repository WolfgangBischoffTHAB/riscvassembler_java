; https://codeburst.io/lets-write-some-harder-assembly-language-code-c7860dcceba

START   LDA     #0       ; zero accumulator
        STA     TMP      ; clear address
        STA     RESULT   ; clear
        STA     RESULT+1 ; clear
        LDX     #8       ; x is a counter
MULT    LSR     MPR      ; shift mpr right - pushing a bit into C
        BCC     NOADD    ; test carry bit
        LDA     RESULT   ; load A with low part of result
        CLC
        ADC     MPD      ; add mpd to res
        STA     RESULT   ; save result
        LDA     RESULT+1 ; add rest off shifted mpd
        ADC     TMP
        STA     RESULT+1
NOADD   ASL     MPD      ; shift mpd left, ready for next "loop"
        ROL     TMP      ; save bit from mpd into temp
        DEX              ; decrement counter
        BNE     MULT     ; go again if counter 0