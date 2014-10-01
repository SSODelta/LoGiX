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

A more complicated example
-----

As LoGiX supports reading files, it's possible to write a text-document which describes the logic behind a puzzle / assignment. Let's look at the puzzle:

Determine who out of the following is guilty of doping. The suspects are: Sam, Michael, Bill, Richard, Matt.
1) Sam said: Michael or Bill took drugs, but not both.
2) Michael said: Richard or Sam took drugs, but not both.
3) Bill said: Matt or Michael took drugs, but not both.
4) Richard said: Bill or Matt took drugs, but not both.
5) Matt said: Bill or Richard took drugs, but not both.
6) Tom said: If Richard took drugs, then Bill took drugs.

Statement 6 is guaranteed to be true, and 4 out of statements 1-5 are false.

To solve this puzzle, we could either be clever and deduce the correct solution, or model this in LoGiX. The source code for 'drugs.txt' is:

```
!!
TrueSam biimplies MichaelDrug xor BillDrug
TrueMichael biimplies RichardDrug xor SamDrug
TrueBill biimplies MattDrug xor MichaelDrug
TrueRichard biimplies BillDrug xor MattDrug
TrueMatt biimplies BillDrug xor RichardDrug
exactly 1 of TrueSam, TrueMichael, TrueBill, TrueRichard, TrueMatt
exactly 1 of MichaelDrug, BillDrug, RichardDrug, SamDrug, MattDrug
RichardDrug implies BillDrug
```

And then when testing who's on drugs, we can write `? {MichaelDrug, BillDrug, RichardDrug, MattDrug, SamDrug}`, which returns `true` only for `SamDrug` implying that Sam is the perpetrator.

Commands
-----

List of valid LoGiX commands and their arguments.
Mandatory arguments are written with `[]`, and optional arguments are written with `()`.
   - `exit`
   - `undo     (DEPTH)`
   - `help     (MODULE)`
   - `load     [FILE_PATH]`
   - `list     [MODULE]`
   - `def      [EXPRESSION]`
   - `exactly  [NUMBER] of [PREMISE1], (PREMISE2), ... (PREMISE_N)`
   - `minimum  [NUMBER] of [PREMISE1], (PREMISE2), ... (PREMISE_N)`
   - `maximum  [NUMBER] of [PREMISE1], (PREMISE2), ... (PREMISE_N)`
   - `example  [FALSE/TRUE] (NUMBER) [EXPRESSION]`
   - `test     [EXPRESSION1] with [EXPRESSION2]`
   - `?        [EXPRESSION]`
   - `num      [EXPRESSION]`
