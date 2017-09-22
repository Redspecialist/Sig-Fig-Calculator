# Sig Fig and Error Propagation Calculator
## Significance
Significant Figures, or sig figs, are scientifically accurate way to present and display measurements in a meaningful way. Sig figs are normally determined by applying the rules addative/subtractive and multiplicative/division significance. However, in the case of higher level analytical chemistry or physics, sig figs are no longer a standalone reference but instead become defined by the uncertainty of a given measurement. Similar to the rules regarding addative/subtractive and multiplicative/division significance there are also equations for propagating uncertainty through similar calculations as well as logarithmic and exponential ones. The following Sig fig calculator looks to provide users with the ability to intutively define formulas and then plug in values to calculate the propogation of uncertainty at the click of a button. Unlike other sigfig calculators one could find online, this calculator includes error propagation for expressions including logarithmic as well as exponential terms.

## Initialization
For those wishing to use the sig fig calculator download and run the executable jar file. Located in the following repository and at this link: [Calculator Download](https://github.com/Redspecialist/Sig-Fig-Calculator/blob/master/Uncertainty_Calculator.jar "Uncertainty Calculater")  
Please make sure that you are running this tool on a computer that has Java 7.0 or higher installed.

## Figures
![alt text](https://github.com/Redspecialist/Sig-Fig-Calculator/blob/master/SigFigCalculator.PNG)  
Here is an example rendering of the sig fig calculator. In order to properly use the following steps:
1. Type in a formula in the formula box specified
2. Click the **Load Expression** button
3. Fill in the fields below with experimental values and specify which values are constants
  a. Any values without an associated uncertainty are considered constants
4. Click **Calculate**
5. When you're done with the current expression click **Clear All**
***Illegal Variable Names***: Log, ln, any name including one of the following characters; ^,+,-,*,/

## Program Functionality
In order to process and calculate an expression in this sig fig calculator, regular expressions to isolate variable names and then store them in a hash map that maps a variable name to a value custom type. The tokenized expression is then interpreted using a context free grammer to build an abstract sytax tree that will then be parsed to calculate the measurements value and it's uncertainty.

## Error
In the event that the GUI fails to work properly on there appears to be a miscalculation please leave a comment on the repository or email me at bm_joyce@live.com with details on which parameters were used.
