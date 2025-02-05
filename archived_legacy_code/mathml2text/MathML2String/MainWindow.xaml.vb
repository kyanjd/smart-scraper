Imports System.IO

Class MainWindow
    Private Async Sub Convert_Click(sender As Object, e As RoutedEventArgs) Handles Convert.Click
        Await WebBrowser1.EnsureCoreWebView2Async()
        Dim sErr As String = ""
        Try
            ' To display the expression inside the browser:
            Dim s As String = "<html><head>" + vbCrLf
            s += "<script src=""https://polyfill.io/v3/polyfill.min.js?features=es6""></script>" + vbCrLf
            s += "<script id=""MathJax-script"" async src=""https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js""></script>" + vbCrLf
            s += "</head><body style='font-size: 1.3em;'>"
            s += tbML.Text + vbCrLf
            s += "</body></html>"
            WebBrowser1.NavigateToString(s)
        Catch ex As Exception
            sErr = ex.Message
        End Try
        Try
            ' Convert into Plain Text:
            tbString.Text = MathMLToString.convertToString(tbML.Text) + vbCrLf
            tbString.Text += sErr
        Catch ex As Exception

        End Try
    End Sub

    Private Sub ConvertToMathML_Click(sender As Object, e As RoutedEventArgs) Handles ConvertToMathML.Click
        Try
            Dim s As String = tbString.Text
            Dim sMathML As String = ConvertStringToMathML.ConvertToMathML(s)
            tbML.Text = sMathML
        Catch ex As Exception

        End Try
    End Sub

    Dim vExamples(-1) As String
    Private Sub MainWindow_Loaded(sender As Object, e As RoutedEventArgs) Handles Me.Loaded
        Try
            ' populate samples
            Dim path As String = IO.Path.GetFullPath("Samples.txt")
            Using fs As New IO.FileStream(path, IO.FileMode.Open, IO.FileAccess.Read)
                Using sr As New IO.StreamReader(fs)
                    vExamples = Split(sr.ReadToEnd(), vbCrLf)
                End Using
            End Using

            Dim lst As New List(Of String)
            lst.AddRange(vExamples)
            cbSamples.ItemsSource = lst
            cbSamples.SelectedIndex = 0

        Catch ex As Exception

        End Try
    End Sub


    Private Sub cbSamples_SelectionChanged(sender As Object, e As SelectionChangedEventArgs)
        Dim n As Int32 = cbSamples.SelectedIndex
        Try
            If n < 0 Then Exit Try
            If Microsoft.VisualBasic.Left(vExamples(n), 2) = "--" Then
                n += 1
            End If
            Dim n0 As Int32 = 0
            Dim i As Int32 = n
            For i = n To vExamples.Length - 1
                If Microsoft.VisualBasic.Left(vExamples(i), 2) = "--" Then
                    n = i - 1
                    Exit For
                End If
            Next
            If i >= vExamples.Length Then n = vExamples.Length - 1
            n0 = 0
            For i = n - 1 To 0 Step -1
                If Microsoft.VisualBasic.Left(vExamples(i), 2) = "--" Then
                    n0 = i + 1
                    Exit For
                End If
            Next
            Dim s As String = ""
            For i = n0 To n
                s += vExamples(i) + vbCrLf
            Next
            If Right(vExamples(n0 - 1), 1) = "-" Then
                tbML.Text = ""
                tbString.Text = s
                ConvertToMathML_Click(Nothing, Nothing)
                Convert_Click(Nothing, Nothing)
            Else
                tbML.Text = s
            End If
            cbSamples.SelectedIndex = n0 - 1
        Catch ex As Exception

        End Try
    End Sub
End Class
