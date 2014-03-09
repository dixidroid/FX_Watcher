FX_Watcher
==========


Виджет для отображения баланса ПАММ счетов с сайта fx-trend.com. Данный виджет каждые 2 часа авторизируется на сайте POST запросом и скачивает GET запросом страницу с балансами, парсит ее и выводит. Для парсинга используется jsoup. В случае, если страница не активна (ведутся работы либо расчеты после РО), выводится сообщение об ошибке. Логин и пароль от аккаунта сохраняется в SharedPreferences. Так же для безопасности рекомендуется включить на сайте подтверждение всех операций по смс.

================================================================================

Widget for displaying balance PAMM accounts from fx-trend.com. This widget every 2 hours authorizes onto the site POST request and GET request download page balances, it parses and displays. Used for parsing jsoup. If the page is not active (operations or calculations after RO), an error message appears. Username and password of your account is stored in SharedPreferences. Just for safety on the site are encouraged to include confirmation of all transactions by SMS.
