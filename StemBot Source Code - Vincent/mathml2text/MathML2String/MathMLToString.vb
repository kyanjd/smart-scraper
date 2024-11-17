Imports System.Text.RegularExpressions

Public Class MathMLToString
    Friend Shared op As String = "\<mo[^\>]*\>" + "(?<op>[^\<]+)" + "\</mo\>"
    Friend Shared num As String = "\<mn[^\>]*\>" + "(?<num>[^\<]+)" + "\</mn\>"
    Friend Shared var As String = "\<mi[^\>]*\>" + "(?<var>[^\<]+)" + "\</mi\>"
    Friend Shared text As String = "\<mtext[^\>]*\>(?<text>[^\<]+)\</mtext\>"

    ' Enssure there will be no nested <mrow>, <mfrac>, <msup, <msqrt> inside <mfrac>...</mfrac>:
    Friend Shared sfrac = "<mfrac[^\>]*>((?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/msqrt>|<mfenced|<\/mfenced>).)*)<\/mfrac>"
    ' Enssure there will be no nested <mrow>, <mfrac>, <msup, <msqrt> inside <msup>...</msup>:
    Friend Shared ssup = "<msup[^\>]*>((?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/msqrt>|<mfenced|<\/mfenced>).)*)<\/msup>"
    ' Enssure there will be no nested <mrow>, <mfrac>, <msup, <msqrt> inside <msqrt>...</msqrt>: 
    Friend Shared ssqrt = "<msqrt[^\>]*>((?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/msqrt>|<mfenced|<\/mfenced>).)*)<\/msqrt>"
    ' Enssure there will be no nested <mrow>, <mfrac>, <msup, <msqrt> inside <mrow>...</mrow>:
    Friend Shared srow = "<mrow[^\>]*>((?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/mrow>|<mfenced|<\/mfenced>).)*)<\/mrow>"
    ' Enssure there will be no nested <mrow>, <mfrac>, <msup, <msqrt> inside <mrow>...</mrow>:
    Friend Shared sfenced = "<mfenced[^\>]*>(?<fenced>(?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/mrow>|<mfenced|<\/mfenced>).)*)<\/mfenced>"
    ' Enssure there will be no nested <msub>, <mrow>, <mfrac>, <msup, <msqrt> inside <msup>...</msup>:
    Friend Shared ssub = "<msub[^\>]*>((?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/msqrt>|<mfenced|<\/mfenced>).)*)<\/msub>"
    Friend Shared sunderover = "<munderover[^\>]*>(?<underover>(?:(?!<mrow|<\/mrow>|<mfrac|<\/mfrac>|<msup|<\/msup>|<msub|<\/msub>|<msqrt|<\/mrow>|<mfenced|<\/mfenced>).)*)<\/munderover>"
    Friend Shared space As String = "<mspace[^\/]*\/>"
    Friend Shared fracB = "@(?<frac>[^@]+)@"
    Friend Shared supB = "\~(?<sup>[^\~]+)~"
    Friend Shared subB = "ç(?<sub>[^ç]+)ç"
    Friend Shared sqrtB = "\¡(?<sqrt>[^\¡]+)¡"
    Friend Shared rowB = "\¿(?<row>[^\¿]+)¿"
    Friend Shared fencedB = "\((?<fenced>[^\¿]+)\)"
    Friend Shared glueOp As String = "(?<op>▒)"
    Friend Shared sqrt As String = "<msqrt[^\\>]*>(?<sqrt>(?:(?!<msqrt>|<\/msqrt>).)*)<\/msqrt>"
    Friend Shared table As String = "\<mtable.*?\>(?<tbl>.*?)\</mtable\>"
    Friend Shared tr As String = "\<mtr.*?\>(?<tr>.*?)\</mtr\>"
    Friend Shared td As String = "\<mtd.*?\>(?<td>.*?)\</mtd\>"
    Friend Shared vStr() As String = New String() {fracB, sqrtB, supB, subB, rowB, op, num, var, text, glueOp}
    Friend Shared reMathML As New Regex(Join(vStr, "|"))
    Friend Shared sReplace() As String = New String() {"\s*", "&\#x22C5;", "&\#8289;", "&\#x2212;", "&\#xB1;",
        "&InvisibleTimes;", "&PlusMinus;", "&int;",
            "&lt;", "&gt;", "&ne;", "&divide;", "&asymp;", "&le", "&ge;",
            "&prop;", "&sum;", "&prod;", "&infin;", "&pi;",
        "&zeta;", "&primes;", "&Gamma;", "&isin;", "&complexes;"}
    Friend Shared sReplaceBy() As String = New String() {"", "*", "", "-", "±",
        "*", "±", "∫",
            "<", ">", "≠", "÷", "≈", "≤", "≥",
            "∝", "∑", "∏", "∞", "П",
        "ζ", "ℙ", "Γ", "∈", "ℂ"}



    Public Shared Function convertToString(sMathML As String) As String
        Dim s As String = ""
        Try
            Dim path As String = IO.Path.GetFullPath("Entities.txt")
            Using fs As New IO.FileStream(path, IO.FileMode.Open, IO.FileAccess.Read)
                Using sr As New IO.StreamReader(fs)
                    Do While Not sr.EndOfStream
                        Dim vEntity() As String = sr.ReadLine.Split(vbTab)
                        If vEntity.Length > 1 AndAlso Len(vEntity(0)) Then
                            sMathML = sMathML.Replace(vEntity(1), vEntity(0))
                        End If
                    Loop
                End Using
            End Using
            Dim j As Int32 = 0
            Dim mcTxt As MatchCollection = Regex.Matches(sMathML, text)
            For Each m As Match In mcTxt
                Dim s1 = m.Groups("text").Value
                s1 = Regex.Replace(s1, "\r\n|\n", "º")
                s1 = Replace(s1, "&nbsp;", "·")
                sMathML = Replace(sMathML, m.Groups("text").Value, s1)
            Next
            sMathML = Regex.Replace(sMathML, space, "&nbsp;")
            For i As Int32 = 0 To sReplace.Length - 1
                sMathML = Regex.Replace(sMathML, sReplace(i), sReplaceBy(i)) ', RegexOptions.IgnoreCase)
            Next
            Dim sRemove As String = "(?i)\<math[^>]*\>|\</math\>|\<mstyle[^>]*\>|\</mstyle\>"
            sMathML = Regex.Replace(sMathML, sRemove, "")

            Dim mtbl As Match = Regex.Match(sMathML, table) ', RegexOptions.IgnoreCase)
            If mtbl.Success Then
                Dim stbl As String = mtbl.Groups("tbl").Value
                Dim mc1 As MatchCollection = Regex.Matches(stbl, tr) ', RegexOptions.IgnoreCase)
                For Each mtr As Match In mc1
                    Dim str As String = mtr.Groups("tr").Value
                    Dim mc2 As MatchCollection = Regex.Matches(str, td)
                    For i As Int32 = 0 To mc2.Count - 1
                        Dim std As String = mc2(i).Groups("td").Value
                        std = FromInnerToOuter(std)
                        Dim mc3 As MatchCollection = reMathML.Matches(std)
                        Dim pML As New ParseML
                        s += pML.Evaluate(mc3)
                        If i < mc2.Count - 1 Then s += "|"
                    Next
                    s += vbCrLf
                Next
            Else
                sMathML = FromInnerToOuter(sMathML)
                Dim mc3 As MatchCollection = reMathML.Matches(sMathML)
                Dim pML As New ParseML
                s += pML.Evaluate(mc3)
            End If
        Catch ex As Exception

        End Try
        Return s
    End Function
    Shared Function FromInnerToOuter(sMath As String) As String
        Try
            Dim hallado As Boolean = True
            Do While hallado
                hallado = False
                Do
                    Dim mfrac As MatchCollection = Regex.Matches(sMath, sfrac)
                    If mfrac.Count = 0 Then Exit Do
                    Dim m As Match = mfrac(0)
                    Dim s1 As String = ParseML.fractionOrExp(m, ParseML.fracExpSqrtType.fraction)
                    s1 = "@" + s1 + "@"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim msup As MatchCollection = Regex.Matches(sMath, ssup)
                    If msup.Count = 0 Then Exit Do
                    Dim m As Match = msup(0)
                    Dim s1 As String = ParseML.fractionOrExp(m, ParseML.fracExpSqrtType.Exponent)
                    s1 = "~" + s1 + "~"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim msub As MatchCollection = Regex.Matches(sMath, ssub)
                    If msub.Count = 0 Then Exit Do
                    Dim m As Match = msub(0)
                    Dim s1 As String = ParseML.fractionOrExp(m, ParseML.fracExpSqrtType.subindex)
                    s1 = "ç" + s1 + "ç"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim msqrt As MatchCollection = Regex.Matches(sMath, ssqrt)
                    If msqrt.Count = 0 Then Exit Do
                    Dim m As Match = msqrt(0)
                    Dim s1 As String = ParseML.fractionOrExp(m, ParseML.fracExpSqrtType.Sqrt)
                    s1 = "¡" + s1 + "¡"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim mrow As MatchCollection = Regex.Matches(sMath, srow)
                    If mrow.Count = 0 Then Exit Do
                    Dim m As Match = mrow(0)
                    Dim s1 As String = ParseML.fractionOrExp(m, ParseML.fracExpSqrtType.Row)
                    s1 = "¿" + s1 + "¿"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim mfenced As MatchCollection = Regex.Matches(sMath, sfenced)
                    If mfenced.Count = 0 Then Exit Do
                    Dim m As Match = mfenced(0)
                    Dim s1 As String = "<mo>(</mo>" + m.Groups("fenced").Value + "<mo>)</mo>"
                    sMath = Replace(sMath, m.Value, s1, 1, 1)
                    hallado = True
                Loop
                Do
                    Dim munderover As MatchCollection = Regex.Matches(sMath, sunderover)
                    If munderover.Count = 0 Then Exit Do
                    Dim mc As MatchCollection = reMathML.Matches(munderover(0).Groups("underover").Value)
                    Dim s1 As String = ""
                    If mc.Count > 2 Then
                        ' there is "from" and "to"
                        s1 = "<msup><msub>" + mc(0).Value _
                        + mc(1).Value + "</msub>" + mc(2).Value + "</msup><mo>▒<mo>"
                    Else
                        ' only "from" (there is no "to")
                        s1 = "<msub>" + mc(0).Value _
                        + mc(1).Value + "</msub>" + "<mo>▒<mo>"
                    End If
                    sMath = Replace(sMath, munderover(0).Value, s1, 1, 1)
                    hallado = True
                Loop
            Loop
        Catch ex As Exception

        End Try
        Return sMath
    End Function
    Private Class ParseML

        Dim iC As Int32
        Dim m, mc(-1) As Match
        Dim sEval As String
        Sub Advance()
            iC += 1
            If iC < mc.Length Then
                m = mc(iC)
            Else
                m = Regex.Match(" ", ".")
            End If
        End Sub
        Public Function Evaluate(mc1 As MatchCollection) As String
            If mc1.Count = 0 Then Return ""
            ReDim mc(mc1.Count - 1)
            mc1.CopyTo(mc, 0)
            For i As Int32 = 0 To mc.Count - 1
                sEval += mc(i).Value
                If (mc(i).Groups("var").Success OrElse mc(i).Groups("num").Success) AndAlso
                i < mc.Count - 1 AndAlso (mc(i + 1).Groups("var").Success OrElse mc(i + 1).Groups("num").Success) Then
                    If Regex.IsMatch(mc(i + 1).Value, "&nbsp;|&emsp;|Γ|∑|∏|∫") Then
                    Else
                        insertOperator("*", i)
                    End If
                    sEval += "*"
                    i += 1
                End If
            Next
            m = mc(0)
            Dim s As String = AddSubs()
            Do While iC < mc.Count
                Dim iC2 As Int32 = iC
                s += AddSubs()
                If iC = iC2 Then Advance()
            Loop
            Return s
        End Function
        Sub insertOperator(sOp As String, im As Int32)
            Try
                ReDim Preserve mc(mc.Length)
                For i As Int32 = mc.Length - 1 To im + 2 Step -1
                    mc(i) = mc(i - 1)
                Next
                mc(im + 1) = MathMLToString.reMathML.Match("<mo>" + sOp + "</mo>")
            Catch ex As Exception

            End Try
        End Sub
        Private Function AddSubs() As String
            Dim s As String = ""
            Try
                s = MultDiv()
                Do While m.Groups("op").Success AndAlso Regex.IsMatch(m.Value, "-|\+|±")
                    Dim sm As String = m.Groups("op").Value
                    Advance()
                    If sm = "+" Then
                        s += "+" + MultDiv()
                    ElseIf sm = "-" Then
                        s += "-" + MultDiv()
                    Else
                        s += "±" + MultDiv()
                    End If
                Loop
                If m.Groups("op").Success Then 'AndAlso m.Groups("op").Value = "=" Then
                    s += m.Groups("op").Value
                    If InStr(m.Value, ")") = 0 Then
                        Advance()
                        s += AddSubs()
                    End If
                End If
            Catch ex As Exception

            End Try
            Return s
        End Function
        Private Function MultDiv() As String
            Dim s As String = ""
            Try
                s = Pow()
                Do While (m.Groups("op").Success AndAlso InStr(m.Value, "*")) OrElse m.Groups("frac").Success
                    If Not m.Groups("frac").Success Then
                        Advance()
                        s += "*" + Pow()
                    Else
                        s += m.Groups("frac").Value
                        Advance()
                    End If
                Loop
            Catch ex As Exception

            End Try
            Return s
        End Function
        Enum fracExpSqrtType
            fraction
            Exponent
            Sqrt
            Row
            subindex
        End Enum
        Public Shared Function fractionOrExp(m As Match, what As fracExpSqrtType) As String
            Dim s As String = ""
            Try
                Dim mRow As MatchCollection = Regex.Matches(m.Value, "<mrow[^\>]*\>(.*?)<\/mrow>")
                Dim sNum, sDen As String
                If mRow.Count = 2 Then
                    Dim mcNum As MatchCollection = MathMLToString.reMathML.Matches(mRow(0).Value)
                    Dim mcRow As MatchCollection = MathMLToString.reMathML.Matches(mcNum(0).Groups("row").Value)
                    Dim pML As New ParseML
                    sNum = pML.Evaluate(mcRow)
                    Dim mcDen As MatchCollection = MathMLToString.reMathML.Matches(mRow(1).Value)
                    mcRow = MathMLToString.reMathML.Matches(mcDen(0).Groups("row").Value)
                    Dim pMLDen As New ParseML
                    sDen = pMLDen.Evaluate(mcRow)
                ElseIf mRow.Count = 1 AndAlso mRow(0).Index = 7 Then
                    Dim mcNum As MatchCollection = MathMLToString.reMathML.Matches(mRow(0).Value)
                    Dim mcRow As MatchCollection = MathMLToString.reMathML.Matches(mcNum(0).Groups("row").Value)
                    Dim pML As New ParseML
                    sNum = pML.Evaluate(mcRow)
                    Dim mcPost As MatchCollection = MathMLToString.reMathML.Matches(Mid(m.Value, mRow(0).Length))
                    pML = New ParseML
                    sDen = pML.Evaluate(mcPost)
                ElseIf mRow.Count = 1 Then
                    Dim pos As Int32 = InStr(m.Value, "<mrow")
                    Dim mcPre As MatchCollection = MathMLToString.reMathML.Matches(Left(m.Value, pos - 1))
                    Dim pMLnum As New ParseML
                    sNum = pMLnum.Evaluate(mcPre)
                    Dim mcDen As MatchCollection = MathMLToString.reMathML.Matches(mRow(0).Value)
                    Dim mcRow As MatchCollection = MathMLToString.reMathML.Matches(mcDen(0).Groups("row").Value)
                    Dim pML As New ParseML
                    sDen = pML.Evaluate(mcDen)
                    If what = fracExpSqrtType.Row Then
                        s += sDen
                        Return s
                    End If
                Else
                    Dim mc As MatchCollection = MathMLToString.reMathML.Matches(m.Value)
                    Dim mcNum As MatchCollection = MathMLToString.reMathML.Matches(mc(0).Value)
                    Dim pMLNum As New ParseML
                    If what = fracExpSqrtType.Sqrt Then
                        sNum = pMLNum.Evaluate(mc)
                        s += "sqrt(" + sNum + ")"
                        Return s
                    Else
                        sNum = pMLNum.Evaluate(mcNum)
                    End If
                    Dim mcDen As MatchCollection = MathMLToString.reMathML.Matches(mc(1).Value)
                    Dim pMLDen As New ParseML
                    sDen = pMLDen.Evaluate(mcDen)
                End If
                If Len(sNum) > 1 AndAlso Regex.IsMatch(Mid(sNum, 2), "[-+±\=]") Then
                    Dim noP As Boolean = Not Regex.IsMatch(Left(sNum, 3), "(∫|∑|∏)_\(")
                    If (sNum.Chars(0) <> "(" OrElse sNum.Chars(Len(sNum) - 1) <> ")") _
                        AndAlso noP Then 'OrElse
                        'InStr(sNum, ")") < Len(sNum) - 1 Then
                        sNum = "(" + sNum + ")"
                    End If
                End If
                If Len(sDen) > 1 AndAlso Regex.IsMatch(Mid(sDen, 2), "[-+*±\=]") Then
                    If sDen.Chars(0) <> "(" OrElse sDen.Chars(Len(sDen) - 1) <> ")" Then ' OrElse
                        'InStr(sDen, ")") < Len(sDen) - 1 Then
                        sDen = "(" + sDen + ")"
                    End If
                End If
                If what = fracExpSqrtType.fraction Then
                    s += sNum + "/" + sDen
                ElseIf what = fracExpSqrtType.subindex Then
                    s += sNum + "_" + sDen
                Else
                    s += sNum + "^" + sDen
                End If

            Catch ex As Exception

            End Try
            Return s
        End Function
        Private Function Pow() As String
            Dim s As String = ""
            Try
                s = Token()
                Do While m.Groups("sup").Success OrElse m.Groups("sub").Success
                    If m.Groups("sup").Success Then
                        Dim sSup As String = m.Groups("sup").Value
                        If Microsoft.VisualBasic.Right(sSup, 1) = "′" Then
                            sSup = Replace(sSup, "^", "")
                        End If
                        s += sSup
                        Advance()
                    Else
                        Dim sSub As String = m.Groups("sub").Value
                        Dim s1() As String = Split(sSub, "_")
                        s += s1(0) + "_" + s1(1)
                        Advance()
                    End If
                Loop
            Catch ex As Exception

            End Try
            Return s
        End Function
        Private Function Token() As String
            Dim s As String = ""
            Do
                s += TokenB()
                If m.Groups("text").Success Then
                    Dim s1 As String = m.Groups("text").Value.Replace("º", "\n")
                    s1 = Replace(s1, "·", " ")
                    s += """" + s1 + """"
                    Advance()
                Else
                    Exit Do
                End If
            Loop
            Return s
        End Function
        Private Function TokenB() As String
            Dim s As String = ""
            Try
                If m.Groups("op").Success AndAlso m.Groups("op").Value = "(" Then
                    Advance()
                    s += "(" + AddSubs() '+ ")"
                    Advance()
                End If
                If m.Groups("op").Success AndAlso m.Groups("op").Value = "-" Then
                    s += m.Groups("op").Value  ' change sign 
                    Advance()
                End If
                If m.Groups("text").Success Then
                    s += m.Groups("text").Value
                    Advance()
                End If
                If m.Groups("row").Success Then
                    s += m.Groups("row").Value
                    Advance()
                End If
                If m.Groups("sqrt").Success Then
                    s += m.Groups("sqrt").Value
                    Advance()
                End If
                'If m.Groups("sup").Success Then
                '    s += m.Groups("sup").Value
                '    Advance()
                'End If
                If m.Groups("var").Success Then
                    s += m.Groups("var").Value
                    Advance()
                ElseIf m.Groups("num").Success Then
                    s += m.Groups("num").Value
                    Advance()
                End If
                If m.Groups("op").Success AndAlso m.Groups("op").Value = "(" Then
                    Advance()
                    s += "(" + AddSubs() '+ ")"
                    Advance()
                End If
            Catch ex As Exception

            End Try
            Return s
        End Function
    End Class
End Class
