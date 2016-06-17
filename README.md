# GramExp

GramExp is a string matching and capturing library with the aim of being more expressive and maintianable than stanard regular expression libraries. GramExp's is based on Parser Expression Grammars described here : http://www.brynosaurus.com/pub/lang/gramExp.pdf

  _"PEGs are stylistically similar to CFGs with RE-like features added, much like Extended Backus-Naur Form (EBNF) notation"_

GramExp is implemented in Parboiled (https://github.com/sirthias/parboiled).

GramExp supports sub-match capturing and can utilise the value stack to balance embedded values.

```java

// classic an bn cn language problem
String grammar =  "D <- &(A !'b') 'a'* B !." +
                        "A <- 'a' A 'b' / :\n" +
                        "B <- 'b' B 'c' / :\n";
try (
  Peg pw = new Peg(grammar);
) {

  for(String input : new String[]{"abc", "aabbcc", "abbc"}) {
    
    boolean match = gramExp.match(input);
    
    System.out.println(input + " : " + (match?"match":"no match"));
  }
}

//basic html parser with named capture example and balanced tags (via push and pop)

String grammar2 =
        "/nlp/\n" +
        "document <- (tag / text)* $\n" +
        "tag <- open_tag (text / tag)* close_tag / self_close \n" +
        "open_tag <- '<' tag_type push (S attr)? '>'\n" +
        "close_tag <- '</' tag_type pop '>'\n" +
        "self_close <- '<' tag_type '/'? '>'\n" +
        "attr <- <[0-9a-zA-Z =\"'#\\[-]+ 'attr'>\n" +
        "tag_type <- <[0-9a-zA-Z]+ 'tag'>\n" +
        "text <- <(!'<'.)+ 'content'>";

try (
        Peg gramExp = new Peg(grammar2);
) {

        System.out.println(gramExp.groups());
        for(String input : new String[]{"<html><body>content<br>new line<br/>another line<br>badgers</body></html>"}) {
        
            System.out.println(gramExp.find(input));
            //[tag=html, tag=body, content=content, tag=br, content=new line, tag=br, content=another line, tag=br, content=badgers, tag=body, tag=html]
        }
}


```


The full syntax and grammar dexscribred in PEG is :
```
# Hierarchical syntax
Grammar <- Spacing Mode? Definition+ EndOfFile
Mode <- '/' (!'/' Char)+ '/' Spacing
Definition <- Identifier LEFTARROW Expression
Expression <- Sequence (SLASH Sequence)*
Sequence <- Prefix*
Prefix <- (AND / NOT)? Suffix / AOPEN Suffix Literal ACLOSE
Suffix <- Primary (QUESTION / STAR / PLUS)? (PUSH / POP)?
Primary <- Identifier !LEFTARROW
/ OPEN Expression CLOSE
/ Literal / Class / DOT / EMPTY / NOTHING
# Lexical syntax
Identifier <- IdentStart IdentCont* Arguments? Spacing
IdentStart <- [a-zA-Z_]
IdentCont <- IdentStart / [0-9]
Arguments <- AOPEN (Identifier / Literal)+ ACLOSE
Literal <- ['] (!['] Char)* ['] Spacing
/ ["] (!["] Char)* ["] Spacing
Class <- '[' (!']' Range)* ']' Spacing
Range <- Char '-' !']' Char / Char
Char <- '\\' [nrt'"\[\]\\]
/ '\\' [0-2][0-7][0-7]
/ '\\' [0-7][0-7]?
/ !'\\' .
LEFTARROW <- '<-' Spacing
SLASH <- '/' Spacing
AND <- '&' Spacing
NOT <- '!' Spacing
QUESTION <- '?' Spacing
STAR <- '*' Spacing
PLUS <- '+' Spacing
OPEN <- '(' Spacing
CLOSE <- ')' Spacing
DOT <- '.' Spacing
EMPTY <- ':' Spacing
NOTHING <- '~' Spacing
COPEN <- '{' Spacing
CCLOSE <- '}' Spacing
AOPEN <- '<' Spacing
ACLOSE <- '>' Spacing
PUSH <- 'push' Spacing
POP <- 'pop' Spacing
Spacing <- (Space / Comment)*
Comment <- '#' (!EndOfLine .)* EndOfLine
Space <- ' ' / '\t' / EndOfLine
EndOfLine <- '\r\n' / '\n' / '\r'
EndOfFile <- !.

```
