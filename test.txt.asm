data segment
    v1002 dw ?
    v1003 dd ?
    v1004 db dup (?)
data ends

code segment
start:
    assume cs:code, ds:data
    mov ax,data
    mov ds,ax
loop0:
    mov ax, 0
    mov v1003, ax
loop1:
    mov ax, v1003
    mov v1002, ax
    jmp loop1
    mov ax, 2
    mov bx, v1003
    mov v1004[bx], ax
    jmp loop0
    mov ax, 4c00h
    int 21h
code ends
end begin
