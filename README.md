LoGiX
=====

LoGiX is a program for determining if certain logical expression are semantic consequences of some premises.
In LoGiX you can define a bunch of premises (assumed true), and determine quickly whether or not a certain other expression is a tautology.

As an example, let's prove that *modus ponens* is a valid argument. Modus ponens says that if Q follows from P, and P is true, then Q is also true. So, in LoGiX we first write:

`def P implies Q`

Which tells LoGiX that Q follows from P. Next up, we write

`def P`

Which tells LoGiX that P is naturally true. Finally, we can ask LoGiX whether or not Q is a tautology based on these premises:

`? Q`

Which gives us the output `true`, telling us that *modus ponens* is a valid argument.
