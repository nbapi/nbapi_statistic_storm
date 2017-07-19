
set mvn_proj = %cd%
echo %mvn_proj%
echo "execute svn update start"
svn update %mvn_proj%
if %errorlevel%==0 (
color 02
echo "==> svn update success!!!"
) else (
color 04
echo "==> svn update fail..."
)

color 07
echo "mvn package start"
call mvn clean package -Pdebug
if %errorlevel%==0 (
color 02
echo "==> mvn package success!!!"
) else (
color 04
echo "==> mvn package fail..."
)
color 07
rem echo :comment_input
rem echo @echo off
rem echo set /p var1=请输入注释内容：
:comment_input
@echo off
set comment=
set /p comment="please input the comment, press ENTER to continue : ":
if not defined comment goto :eof
set count=0
setlocal enabledelayedexpansion
:intercept
set /a count+=1
for /f %%i in ("%count%") do if not "!comment:~%%i,1!"=="" goto intercept
echo length: %count%

 if %count% lss 10 (
    echo "the comment's length is less than 10 chars, please try agin ... "
    goto :comment_input
 )


rem svn commit -m %comment% %mvn_proj%
svn commit -m %comment% %mvn_proj%
@echo off
if %errorlevel%==0 (
color 02
echo "==> svn commit success!!!"
) else (
color 04
echo "==> svn commit fail..."
)
pause
color 07
:error
cmd /C exit /B
