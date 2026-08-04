package com.aistudio.kidspolice.abcd.ui

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.kidspolice.abcd.R
import java.util.Locale

data class StoryScene(
    val text: String,
    val imageResId: Int
)

data class IllustratedStory(
    val title: String,
    val description: String,
    val icon: String,
    val bgColor: Color,
    val contentColor: Color,
    val scenes: List<StoryScene>,
    val moral: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val isGenerating by viewModel.isGeneratingStory.collectAsStateWithLifecycle()
    val generatedStory by viewModel.generatedStory.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale("ar")
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isSpeaking = true
                    }
                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                    }
                    override fun onError(utteranceId: String?) {
                        isSpeaking = false
                    }
                })
                tts = textToSpeech
            }
        }
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    LaunchedEffect(generatedStory) {
        if (generatedStory == null) {
            tts?.stop()
            isSpeaking = false
        }
    }

    // Illustrated Stories data
    val illustratedStories = remember {
        listOf(
            IllustratedStory(
                title = "رحلة دانا السحرية في الفضاء",
                description = "دانا الطموحة تطير في الفضاء وتصادق النجوم السعيدة!",
                icon = "🚀",
                bgColor = Color(0xFFEEF2FF),
                contentColor = Color(0xFF4F46E5),
                scenes = listOf(
                    StoryScene(
                        text = "كانت الطفلة دانا تحب النجوم كثيراً. في كل ليلة، تقف بجانب نافذتها وتنظر بتلسكوب كرتوني ملون صنعته بنفسها، وتحلم بأن تطير يوماً ما بين الكواكب والمجرات الساطعة.",
                        imageResId = R.drawable.img_story_space_1_1783527560377
                    ),
                    StoryScene(
                        text = "وفي إحدى الليالي، غطت دانا في نوم عميق، فرأت في منامها أنها تركب سفينة فضاء سحرية مبهجة وتطير عالياً في الفضاء الفسيح، وتلوّح للنجوم الضاحكة المضيئة التي ترحب بها بسعادة بالغة في رحلتها الشيقة.",
                        imageResId = R.drawable.img_story_space_2_1783527571835
                    )
                ),
                moral = "الأحلام الكبيرة تبدأ بشغف صغير، وبالعزيمة نصل للنجوم!"
            ),
            IllustratedStory(
                title = "أرنوب وسر الجزرة الذهبية",
                description = "أرنوب وأصدقاؤه يتعلمون قوة التعاون والمشاركة والمحبة!",
                icon = "🥕",
                bgColor = Color(0xFFFFF7ED),
                contentColor = Color(0xFFEA580C),
                scenes = listOf(
                    StoryScene(
                        text = "في حقل أخضر جميل، وجد الأرنب أرنوب جزرة ذهبية عملاقة تلمع تحت أشعة الشمس البراقة. كان أرنوب مندهشاً جداً وسعيداً بهذا الاكتشاف المذهل ويبحث عن طريقة لاستخراجها من الأرض.",
                        imageResId = R.drawable.img_story_carrot_1_1783527583307
                    ),
                    StoryScene(
                        text = "حاول أرنوب سحب الجزرة بمفرده لكنه لم يستطع لثقلها، فدعا صديقه السنجاب والقنفذ الصغير. تعاونوا معاً بكل قوتهم وحبهم حتى نجحوا في استخراجها، وجلسوا في بيت الشجرة الدافئ يتقاسمون الجزرة اللذيذة بسعادة، وتعلموا أن التعاون هو سر النجاح الحقيقي!",
                        imageResId = R.drawable.img_story_carrot_2_1783527595200
                    )
                ),
                moral = "في الاتحاد قوة، وبالمشاركة تكتمل السعادة!"
            ),
            IllustratedStory(
                title = "الأسد الطيب والفأر الصغير",
                description = "قصة تعلم الأطفال أن حتى أصغر الكائنات يمكنه تقديم مساعدة عظيمة!",
                icon = "🦁",
                bgColor = Color(0xFFFEFCE8),
                contentColor = Color(0xFFCA8A04),
                scenes = listOf(
                    StoryScene(
                        text = "في غابة واسعة وجميلة، كان الأسد القوي ينام بهدوء تحت ظل شجرة ضخمة. وفجأة، بدأ فأر صغير ذو لون بني يلعب بفضول بالقرب من وجه الأسد، مما أيقظه من نومه اللذيذ.",
                        imageResId = R.drawable.img_story_lion_1_1783540693317
                    ),
                    StoryScene(
                        text = "بعد فترة، وقع الأسد في شبكة الصيادين ولم يستطع الإفلات. فسمعه الفأر الصغير وأسرع إليه وقام بقضم الحبال بأسنان القوية بكل إصرار حتى قطعه تماماً ونجا الأسد، فضحك الأسد وشكر الفأر وتعلم أن الرفق والخير لا يضيع أبداً!",
                        imageResId = R.drawable.img_story_lion_2_1783540705890
                    )
                ),
                moral = "لا تستصغر أحداً، فكل شخص لديه قوة خفية يمكن أن تنقذك يوماً!"
            ),
            IllustratedStory(
                title = "الغزالة الذكية والينبوع السحري",
                description = "الغزالة الصغيرة تكتشف سحر الطبيعة وتشارك أصدقاءها الخير!",
                icon = "🦌",
                bgColor = Color(0xFFF0FDF4),
                contentColor = Color(0xFF16A34A),
                scenes = listOf(
                    StoryScene(
                        text = "كانت الغزالة الصغيرة ريم ذات العيون الواسعة الجميلة تتجول في الغابة الكثيفة بحثاً عن ماء عذب، حتى وقعت عيناها على ينبوع ماء سحري يلمع كالبلور الصافي بين الأشجار الخضراء الزاهية.",
                        imageResId = R.drawable.img_story_gazelle_1_1783540718337
                    ),
                    StoryScene(
                        text = "فرحت ريم كثيراً، لكنها لم تشرب بمفردها، بل ركضت ونادت صديقها الغزال والطيور الملونة المغردة ليشربوا معاً تحت قوس قزح بديع الألوان، وعاشت الحيوانات في أمان وحب وتآلف دائم.",
                        imageResId = R.drawable.img_story_gazelle_2_1783540729392
                    )
                ),
                moral = "الكرم ومشاركة الخير مع الآخرين يضاعف السعادة!"
            ),
            IllustratedStory(
                title = "العصفور كوكو يتعلم الطيران",
                description = "رحلة العصفور الصغير في التغلب على خوفه والمحاولة من جديد!",
                icon = "🐦",
                bgColor = Color(0xFFF0F9FF),
                contentColor = Color(0xFF0284C7),
                scenes = listOf(
                    StoryScene(
                        text = "وقف العصفور الأزرق الصغير كوكو على غصن شجرة عالٍ، ينظر بتردد وخوف إلى السماء الواسعة والغيوم القطنية البيضاء، خائفاً من بسط جناحيه الصغيرين لتجربة الطيران الأول له.",
                        imageResId = R.drawable.img_story_bird_1_1783540744488
                    ),
                    StoryScene(
                        text = "أغمض كوكو عينيه، وأخذ نفساً عميقاً ثم قفز بشجاعة! بدأ يرفرف بجناحيه بقوة، وفجأة وجد نفسه يطير ويحلق عالياً في السماء الدافئة بجانب الطيور السعيدة الأخرى، شاعراً بفرحة الانتصار على الخوف.",
                        imageResId = R.drawable.img_story_bird_2_1783540754951
                    )
                ),
                moral = "الشجاعة هي المحاولة رغم الخوف، والنجاح يحتاج إصرار!"
            ),
            IllustratedStory(
                title = "السلحفاة سريعة والسباق الممتع",
                description = "سريعة تثبت للجميع أن الصبر والمثابرة هما سر الوصول للهدف!",
                icon = "🐢",
                bgColor = Color(0xFFFAF5FF),
                contentColor = Color(0xFF7E22CE),
                scenes = listOf(
                    StoryScene(
                        text = "قررت السلحفاة الصغيرة \"سريعة\" التي ترتدي قبعة حمراء جميلة أن تشارك في سباق الغابة السنوي. وقفت بثقة وابتسامة عند خط البداية المرسوم في مرج أخضر واسع ومليء بالأمل والنشاط.",
                        imageResId = R.drawable.img_story_turtle_1_1783540766001
                    ),
                    StoryScene(
                        text = "رغم بطئها المعهود، لم تتوقف سريعة للحظة واحدة، بينما نام الأرنب المغرور في منتصف الطريق. وصلت سريعة لخط النهاية المزين بالزهور ليعلن فوزها وسط هتاف وتصفيق الحيوانات السعيدة ونثر الألوان!",
                        imageResId = R.drawable.img_story_turtle_2_1783540778580
                    )
                ),
                moral = "المثابرة والصبر يوصلانك للهدف دائماً، مهما كنت بطيئاً!"
            ),
            IllustratedStory(
                title = "السمكة الصغيرة والشعاب المرجانية",
                description = "لولو السمكة الشجاعة تستكشف أسرار البحار وتجد كنوزاً مذهلة!",
                icon = "🐟",
                bgColor = Color(0xFFE0F2FE),
                contentColor = Color(0xFF0369A1),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "كانت السمكة لولو تحلم دائماً بما يوجد خلف الشعاب المرجانية الكبيرة. ورغم صغر حجمها، قررت يوماً أن تبدأ رحلتها الاستكشافية، فودعت أصدقاءها وانطلقت تسبح برشاقة بين المرجان الملون البديع." 
                        else "وبينما كانت تستكشف كهفاً بحرياً غامضاً، وجدت لؤلؤة عملاقة تشع نوراً سحرياً يملأ المكان جمالاً. أدركت لولو أن الشجاعة تجعلنا نكتشف كنوزاً لم نكن نتخيلها أبداً في أعماق البحار.",
                        imageResId = if (index == 0) R.drawable.img_story_fish_1_1784051558258 else R.drawable.img_story_fish_2_1784051651733
                    )
                },
                moral = "الشجاعة والفضول يفتحان لنا أبواباً لعوالم جديدة مذهلة!"
            ),
            IllustratedStory(
                title = "النحلة نشيطة وسر العسل",
                description = "نشيطة تتعاون مع صديقاتها لصنع العسل وتتعلم قيمة العمل الجماعي!",
                icon = "🐝",
                bgColor = Color(0xFFFEFCE8),
                contentColor = Color(0xFFA16207),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "كانت النحلة نشيطة تحب عملها كثيراً، وتطير كل صباح من زهرة إلى أخرى لتجمع الرحيق. كانت تغني بسعادة وهي تحط على زهور عباد الشمس الكبيرة التي تبتسم لها تحت أشعة الشمس الذهبية."
                        else "عادت نشيطة إلى الخلية، وتعاونت مع صديقاتها النحلات في تحويل الرحيق إلى عسل لذيذ ومفيد. تعلمت نشيطة أن العمل الجماعي المنظم هو الذي يصنع أحلى النتائج التي يستمتع بها الجميع.",
                        imageResId = if (index == 0) R.drawable.img_story_bee_1_1784051580432 else R.drawable.img_story_bee_2_1784051592445
                    )
                },
                moral = "العمل بجد والتعاون مع الآخرين يثمر دائماً عن نتائج رائعة!"
            ),
            IllustratedStory(
                title = "الفيل فلفول والزهرة السحرية",
                description = "فلفول الطيب يعتني بالزهرة الصغيرة ويحول الغابة إلى مكان ساحر!",
                icon = "🐘",
                bgColor = Color(0xFFF1F5F9),
                contentColor = Color(0xFF475569),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "كان الفيل فلفول يملك قلباً طيباً جداً. وفي يوم من الأيام، وجد زهرة زرقاء صغيرة ذابلة في الغابة، فقرر أن يسقيها بلطف باستخدام خرطومه الطويل، ويعتني بها يومياً بكل حب وصبر."
                        else "وبعد فترة من الاهتمام، نمت الزهرة لتصبح زهرة سحرية عملاقة تشع أنواراً مبهجة، ونشرت عطراً جميلاً في كل الغابة. شكرت الزهرة فلفول على كرمه، وأدرك الجميع أن العطف على الصغير يثمر خيراً كبيراً.",
                        imageResId = if (index == 0) R.drawable.img_story_elephant_1_1784051603314 else R.drawable.img_story_elephant_2_1784051614205
                    )
                },
                moral = "اللطف والاهتمام بالكائنات الضعيفة يصنع معجزات جميلة!"
            ),
            IllustratedStory(
                title = "القطة مشمشة وحلم الطيران",
                description = "مشمشة المبدعة تجد طريقتها الخاصة للطيران والاستمتاع بمواهبها!",
                icon = "🐱",
                bgColor = Color(0xFFFFF7ED),
                contentColor = Color(0xFFC2410C),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "كانت القطة مشمشة تجلس على السور وتراقب العصافير وهي تحلق في السماء، وتتمنى لو كانت تملك أجنحة لتطير مثلهم. كانت تغمض عينيها وتتخيل نفسها تسبح في الهواء فوق الأشجار العالية."
                        else "قررت مشمشة أن تصنع وشاحاً من أوراق الشجر الكبيرة، وبدأت تقفز بين أغصان الشجرة العالية بمهارة وفرح. شعرت مشمشة أنها تطير حقاً، وتعلمت أن السعادة تكمن في الاستمتاع بما نملك وبقدراتنا الخاصة.",
                        imageResId = if (index == 0) R.drawable.img_story_cat_1_1784051625352 else R.drawable.img_story_cat_2_1784051637617
                    )
                },
                moral = "الرضا بما لدينا والإبداع يجعلنا نعيش أحلامنا بطريقتنا الخاصة!"
            ),
            IllustratedStory(
                title = "البومة الحكيمة ودرس الوقت",
                description = "البومة تعلم الحيوانات قيمة الوقت وكيفية تنظيمه للنجاح والسعادة!",
                icon = "🦉",
                bgColor = Color(0xFFEEF2FF),
                contentColor = Color(0xFF3730A3),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "تحت ضوء القمر الفضي، كانت البومة الحكيمة تجلس على غصنها القديم وتراقب النجوم. كانت تعرف أسرار الغابة، وتحب أن تشارك حكمتها مع الحيوانات الصغيرة التي تجتمع حولها في المساء."
                        else "علمت البومة أصدقاءها كيف ينظمون وقتهم بين اللعب والعمل والراحة باستخدام ساعة رملية مصنوعة من الأحجار. أدركت الحيوانات أن احترام الوقت هو سر النجاح والسعادة في مملكتم الهادئة.",
                        imageResId = if (index == 0) R.drawable.img_story_owl_1_1784051662335 else R.drawable.img_story_owl_2_1784051675008
                    )
                },
                moral = "تنظيم الوقت واحترام المواعيد يجعل حياتنا أكثر نظاماً ونجاحاً!"
            ),
            IllustratedStory(
                title = "الجمل صابر ونجمة الصحراء",
                description = "صابر الجمل الصبور يصل لهدفه عبر الصحراء مسترشداً بالنجوم!",
                icon = "🐪",
                bgColor = Color(0xFFFFFBEB),
                contentColor = Color(0xFFB45309),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "في قلب الصحراء الذهبية الشاسعة، كان الجمل صابر يسير بصبر وعزيمة تحت شمس الغروب الأرجوانية. كان صابر يعرف الطريق جيداً، ويتحمل العطش والتعب ليصل إلى الواحة الخضراء الجميلة."
                        else "وعندما حل الليل، استرشد صابر بنجمة ساطعة في السماء دلته على الطريق الصحيح. وصل صابر بسلام، وتعلم أن الصبر والهدوء هما رفيقا الطريق للوصول إلى أي هدف مهما كان بعيداً.",
                        imageResId = if (index == 0) R.drawable.img_story_camel_1_1784051688010 else R.drawable.img_story_camel_2_1784051699917
                    )
                },
                moral = "الصبر والتحمل هما مفتاح الوصول للأهداف الكبيرة!"
            ),
            IllustratedStory(
                title = "السنجاب فرفور وسر الشتاء",
                description = "فرفور يستعد للشتاء ويتعلم أن الكرم يضاعف البركة والسعادة!",
                icon = "🐿️",
                bgColor = Color(0xFFFDF2F8),
                contentColor = Color(0xFFBE185D),
                scenes = List(2) { index ->
                    StoryScene(
                        text = if (index == 0) "مع اقتراب فصل الشتاء، بدأ السنجاب فرفور بجمع ثمار البلوط وتخزينها في بيته الدافئ داخل الشجرة. كان يعمل بنشاط طوال الخريف ليضمن توفر الطعام له ولأسرته في الأيام الباردة."
                        else "وعندما نزل الثلج، وجد فرفور أرنباً صغيراً جائعاً، فدعاه لبيته وتقاسم معه طعامه ودفئه. شعر فرفور بسعادة غامرة، وتعلم أن المشاركة في وقت الشدة تضاعف البركة والمحبة بين الأصدقاء.",
                        imageResId = if (index == 0) R.drawable.img_story_squirrel_1_1784051712473 else R.drawable.img_story_squirrel_2_1784051724262
                    )
                },
                moral = "الاستعداد للمستقبل والمشاركة مع المحتاجين تجلب السعادة الحقيقية!"
            )
        )
    }

    val storyTopics = listOf(
        StoryTopic("الصدق والأمانة", "🌟", Color(0xFFFEF3C7), Color(0xFFD97706)),
        StoryTopic("التعاون والمساعدة", "🤝", Color(0xFFF0FDF4), Color(0xFF16A34A)),
        StoryTopic("الشجاعة والبطولة", "🦸‍♂️", Color(0xFFEFF6FF), Color(0xFF2563EB)),
        StoryTopic("احترام الوالدين", "❤️", Color(0xFFFEF2F2), Color(0xFFDC2626)),
        StoryTopic("حب العلم والقراءة", "📚", Color(0xFFFAF5FF), Color(0xFF9333EA)),
        StoryTopic("الرفق بالحيوان", "🐾", Color(0xFFFFFBEB), Color(0xFFD97706))
    )

    // Selection States
    var selectedIllustratedStory by remember { mutableStateOf<IllustratedStory?>(null) }
    var currentSceneIndex by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Illustrated, 1: AI Gemini

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("قصص الحكمة والأخلاق", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedIllustratedStory != null) {
                            tts?.stop()
                            isSpeaking = false
                            selectedIllustratedStory = null
                            currentSceneIndex = 0
                        } else if (generatedStory != null || isGenerating) {
                            tts?.stop()
                            isSpeaking = false
                            viewModel.clearStory()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color(0xFF1E3A8A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEFF6FF))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedIllustratedStory != null) {
                // Show Illustrated Story Reader
                val story = selectedIllustratedStory!!
                val currentScene = story.scenes[currentSceneIndex]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Scene counter & TTS Speak Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (isSpeaking) {
                                        tts?.stop()
                                        isSpeaking = false
                                    } else {
                                        tts?.language = Locale("ar")
                                        tts?.speak(currentScene.text, TextToSpeech.QUEUE_FLUSH, null, "scene")
                                        isSpeaking = true
                                    }
                                },
                                modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(50))
                            ) {
                                Icon(
                                    if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = if (isSpeaking) "إيقاف القراءة" else "قراءة بصوت ديدي",
                                    tint = Color(0xFF3B82F6)
                                )
                            }

                            Text(
                                text = "المشهد ${currentSceneIndex + 1} من ${story.scenes.size}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Illustration Image
                        Image(
                            painter = painterResource(id = currentScene.imageResId),
                            contentDescription = story.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Text Content
                        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            item {
                                Text(
                                    text = currentScene.text,
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                if (currentSceneIndex == story.scenes.size - 1) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = story.contentColor.copy(alpha = 0.1f)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "🌟 الحكمة من القصة 🌟",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = story.contentColor,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = story.moral,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF334155),
                                                textAlign = TextAlign.Center,
                                                lineHeight = 24.sp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scene Nav Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TTS Button
                    FloatingActionButton(
                        onClick = {
                            if (isSpeaking) {
                                tts?.stop()
                                isSpeaking = false
                            } else {
                                val speakText = if (currentSceneIndex == story.scenes.size - 1) {
                                    "${currentScene.text}. الحكمة من القصة هي: ${story.moral}"
                                } else {
                                    currentScene.text
                                }
                                tts?.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, "story_utterance")
                                isSpeaking = true
                            }
                        },
                        containerColor = story.contentColor,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Read Aloud"
                        )
                    }

                    Button(
                        onClick = {
                            tts?.stop()
                            isSpeaking = false
                            if (currentSceneIndex < story.scenes.size - 1) {
                                currentSceneIndex++
                            } else {
                                selectedIllustratedStory = null
                                currentSceneIndex = 0
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (currentSceneIndex < story.scenes.size - 1) "المشهد التالي ⬅️" else "تمت القراءة يا بطل! 🎉",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (currentSceneIndex > 0) {
                        OutlinedButton(
                            onClick = {
                                tts?.stop()
                                isSpeaking = false
                                currentSceneIndex--
                            },
                            modifier = Modifier
                                .weight(0.7f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6))
                        ) {
                            Text(
                                text = "➡️ المشهد السابق",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }
                }

            } else if (generatedStory != null || isGenerating) {
                // Show AI Gemini Generated Story
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color(0xFF3B82F6))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "كابتن ديدي يؤلف لك قصة ممتعة الآن... ⏳",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (isSpeaking) {
                                                    tts?.stop()
                                                    isSpeaking = false
                                                } else {
                                                    generatedStory?.let {
                                                        tts?.language = Locale("ar")
                                                        tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "story")
                                                        isSpeaking = true
                                                    }
                                                }
                                            },
                                            modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(50))
                                        ) {
                                            Icon(
                                                if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = if (isSpeaking) "إيقاف القراءة" else "قراءة القصة",
                                                tint = Color(0xFF3B82F6)
                                            )
                                        }
                                    }
                                    Text(
                                        text = generatedStory ?: "",
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp,
                                        color = Color(0xFF1E293B),
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.clearStory() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("قراءة قصة أخرى", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val tabs = listOf("قصص مصورة حديثة 📖", "تأليف بالذكاء الاصطناعي 🤖")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    // List Illustrated Stories
                    Text(
                        "اختر قصة مصورة وممتعة لتبدأ القراءة والتعلم يا بطل:",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(illustratedStories) { story ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedIllustratedStory = story
                                        currentSceneIndex = 0
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = story.bgColor),
                                border = androidx.compose.foundation.BorderStroke(1.dp, story.contentColor.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f).padding(end = 12.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = story.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = story.contentColor,
                                            textAlign = TextAlign.Right
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = story.description,
                                            fontSize = 13.sp,
                                            color = Color(0xFF475569),
                                            textAlign = TextAlign.Right
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = story.icon, fontSize = 28.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // List AI Gemini Topics
                    Text(
                        "اختر موضوع القصة التي تود سماعها من كابتن ديدي يا بطل:",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(storyTopics) { topic ->
                            StoryTopicCard(topic) {
                                viewModel.generateStory(topic.title)
                            }
                        }
                    }
                }

                // AdMob Banner Ad
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

data class StoryTopic(val title: String, val icon: String, val bgColor: Color, val contentColor: Color)

@Composable
fun StoryTopicCard(topic: StoryTopic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = topic.bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, topic.contentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = topic.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = topic.contentColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = topic.icon, fontSize = 24.sp)
            }
        }
    }
}
