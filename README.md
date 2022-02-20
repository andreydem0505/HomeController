# Home Controller

Мобильное приложение для операционной системы Android, анализирующее показания датчиков устройства и сообщающее пользователю о больших изменениях. Может использоваться как охранная система, а также для проведения научных исследований.

Системные требования: Android версии 8 и выше.

В ходе разработки использовались современные и перспективные технологии: язык программирования Kotlin, фреймворк Spring, библеотеки Jetpack Compose и Retrofit, облачная NoSQL база данных Cloud Firestore, система сборки Gradle, система управления версиями Git, стиль графического дизайна Material Design, API Telegram.

Использовались приёмы объектно-оринтированного, аспектно-ориентированного и функционального программирования. Одной из особенностей приложения является реактивный интерфейс.

Мобильное приложение связывается с серверной частью посредством Rest API. Серверная часть отвечает за работу Telegram бота и хранение данных в базе.

Ссылка на серверную часть: https://github.com/andreydem0505/HomeController-Backend
<hr>

#### Анализирующая система имеет следующие особенности:
- Датчик определяет разницу между показаниями, при которой надо поднять тревогу, самостоятельно на основе первых показаний и запоминает как ultimateDifference. При превышении очередной разницы между соседними показаниями над ultimateDifference отправляется сигнал тревоги пользователю в виде сообщения в Telegram боте.
- Алгоритм решает проблему постоянных небольших колебаний датчиков и позволяет системе приспособиться к окружающей среде. Так, если в комнате моргает лампочка, датчик освещения не будет реагировать на это, но будет сообщать о действительно подозрительных изменениях.
- Решение позволяет добавлять обработку новых датчиков в приложение без того, чтобы задумываться об их диапазоне значений и чувствительности, так как это берёт на себя интеллект системы.
- Каждому датчику прикрепляется свой объект класса SensorListener. Это позволяет проводить анализ данных для каждого датчика непосредственно от другого (иметь своё значение ultimateDifference).
- Пользователь может настраивать систему под себя, например, изменяя коэффициент чувствительности.
- Алгоритм не потребляет много ресурсов, работа может происходить в фоновом режиме.
- Подобный алгоритм также возможно использовать для других задач, таких как обработка значений температуры воздуха, курсов валют и т. д., поэтому решение является универсальным.
<hr>

#### Пользовательская история:
1. Если пользователь авторизован, смотрите следующий шаг. Иначе открывается экран с инструкцией по регистрации и ссылкой на Telegram бота. Пользователь переходит в бота, запускает его и получает индивидуальный уникальный ключ. Пользователь переходит обратно в приложение и вставляет ключ в соответствующее поле. Ключ проверяется и, в случае успеха, записывается в локальное хранилище приложения, и экран закрывается. С этого момента пользователь считается авторизованным.
2. Пользователь изменяет настройки приложения, такие как коэффициент чувствительности и время задержи перед началом анализа окружающей среды. При первом открытии этого экрана в поля подставляются рекомендуемые значения, при последующих - значения, которые пользователь ввёл в прошлый раз (записываются в локальное хранилище). Пользователь может узнать больше о конкретном поле, нажав на соответствующую иконку. В этом случае откроется диалоговое окно с информацией и кнопкой "ОК" для закрытия этого окна. При неверном формате ввода граница соответствующего поля становится красной, появляется надпись об ошибке, кнопка "Запустить" блокируется. После настроек, пользователь нажимает кнопку "Запустить".
3. Если пользователь оставил значение задержки равное 0, смотрите следующий шаг. Иначе на экране появляется запущенный таймер, ведущий отсчёт с выставленного значения задержки. Пользователь в это время устанавливает устройство в выделенное для него место, оставляет окружающие условия такими, какими они должны быть во время работы системы (например, уходит из помещения, выключив за собой свет, закрыв дверь и т. д.).
4. Открывается экран, показывающий текущие показания датчиков, имеющиеся на устройстве и обрабатываемые приложением. Система начинает анализ окружающей среды. Если какой-то датчик показывает сильные изменения, его название и показания становятся красными на 5 секунд, а пользователю отправляется сообщение о тревоге в Telegram боте. Пользователю не приходят сообщения о тревоге с одного датчика чаще, чем раз в 10 минут. Вернувшись к устройству, пользователь может закрыть приложение или закрыть текущий экран (в этом случае смотрите шаг 2).
