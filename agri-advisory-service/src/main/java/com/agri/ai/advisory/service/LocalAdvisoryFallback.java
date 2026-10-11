package com.agri.ai.advisory.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocalAdvisoryFallback {

    public String answer(String query, String language) {
        if (isCoconutCareQuestion(query)) {
            return coconutChecklist(language);
        }
        if (isApricotDefinition(query)) {
            return apricotDefinition(language);
        }
        if (isCoconutDefinition(query)) {
            return coconutDefinition(language);
        }
        return unavailableMessage(language);
    }

    private boolean isCoconutCareQuestion(String query) {
        String text = query.toLowerCase(Locale.ROOT);
        boolean coconut = containsAny(text, "coconut", "தேங்க", "नारियल",
                "തെങ്ങ", "ತೆಂಗ", "కొబ్బరి", "नारळ");
        boolean asksForCare = containsAny(text, "maintain", "care", "grow", "cultivat",
                "fertiliz", "water", "manage", "பராமர", "வளர", "देखभाल", "उगाना",
                "പരിപാല", "വളർത്ത", "ನಿರ್ವಹ", "ಬೆಳೆ", "పెంచ", "పరిరక్ష", "काळजी", "लागवड");
        return coconut && asksForCare;
    }

    private boolean isApricotDefinition(String query) {
        String text = query.toLowerCase(Locale.ROOT);
        return containsAny(text, "apricot", "खुबानी", "abricot", "abrikoos")
                && containsAny(text, "what", "meaning", "define", "fruit", "என்ன", "क्या", "అంటే");
    }

    private boolean isCoconutDefinition(String query) {
        String text = query.toLowerCase(Locale.ROOT);
        return containsAny(text, "coconut", "தேங்க", "नारियल", "തെങ്ങ",
                "ತೆಂಗ", "కొబ్బరి", "नारळ")
                && containsAny(text, "what", "meaning", "define", "என்ன", "क्या", "అంటే");
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) {
            if (text.contains(term)) return true;
        }
        return false;
    }

    private String unavailableMessage(String language) {
        return switch (language) {
            case "Tamil" -> "இந்தக் கேள்விக்கு நம்பகமான பதில் அளிக்க Gemini AI தேவை; அது இப்போது அமைக்கப்படவில்லை. தொடர்பில்லாத ஆலோசனை வழங்காமல் நிறுத்துகிறேன். வேறு ஒரு கேள்வியை முயற்சிக்கவும் அல்லது புதிய Google AI Studio key-ஐ root .env கோப்பில் வைத்து Advisory சேவையை மறுதொடக்கம் செய்யவும்.";
            case "Hindi" -> "इस प्रश्न का भरोसेमंद उत्तर देने के लिए Gemini AI चाहिए, लेकिन यह अभी कॉन्फ़िगर नहीं है। असंबंधित सलाह देने के बजाय मैं यहीं रुक रहा हूँ। दूसरा प्रश्न पूछें, या नई Google AI Studio key को root .env में रखकर Advisory सेवा पुनः शुरू करें।";
            case "Malayalam" -> "ഈ ചോദ്യത്തിന് വിശ്വസനീയമായ മറുപടി നൽകാൻ Gemini AI ആവശ്യമാണ്; അത് ഇപ്പോൾ ക്രമീകരിച്ചിട്ടില്ല. ബന്ധമില്ലാത്ത ഉപദേശം നൽകാതെ ഇവിടെ നിർത്തുന്നു. മറ്റൊരു ചോദ്യം ചോദിക്കാം, അല്ലെങ്കിൽ പുതിയ Google AI Studio key root .env-ൽ ചേർത്ത് Advisory സേവനം വീണ്ടും ആരംഭിക്കുക.";
            case "Kannada" -> "ಈ ಪ್ರಶ್ನೆಗೆ ವಿಶ್ವಾಸಾರ್ಹ ಉತ್ತರಿಸಲು Gemini AI ಅಗತ್ಯವಿದೆ; ಅದು ಈಗ ಹೊಂದಿಸಿಲ್ಲ. ಸಂಬಂಧವಿಲ್ಲದ ಸಲಹೆ ನೀಡದೆ ಇಲ್ಲಿ ನಿಲ್ಲಿಸುತ್ತೇನೆ. ಬೇರೆ ಪ್ರಶ್ನೆ ಕೇಳಿ ಅಥವಾ ಹೊಸ Google AI Studio key ಅನ್ನು root .env ಗೆ ಸೇರಿಸಿ Advisory ಸೇವೆಯನ್ನು ಮರುಪ್ರಾರಂಭಿಸಿ.";
            case "Telugu" -> "ఈ ప్రశ్నకు నమ్మదగిన సమాధానం ఇవ్వడానికి Gemini AI అవసరం; అది ప్రస్తుతం అమర్చలేదు. సంబంధంలేని సలహా ఇవ్వకుండా ఇక్కడ ఆపుతున్నాను. మరో ప్రశ్న అడగండి లేదా కొత్త Google AI Studio keyని root .envలో ఉంచి Advisory సేవను మళ్లీ ప్రారంభించండి.";
            case "Marathi" -> "या प्रश्नाचे विश्वासार्ह उत्तर देण्यासाठी Gemini AI आवश्यक आहे; ते सध्या कॉन्फिगर केलेले नाही. असंबंधित सल्ला देण्याऐवजी मी इथे थांबतो. दुसरा प्रश्न विचारा किंवा नवीन Google AI Studio key root .env मध्ये ठेवून Advisory सेवा पुन्हा सुरू करा.";
            default -> "I can't answer that reliably because Gemini AI is not configured. Rather than give you unrelated advice, I’m stopping here. Try a supported local question, or add a new Google AI Studio key to the root .env file and restart the Advisory service for open-ended AI answers.";
        };
    }

    private String apricotDefinition(String language) {
        return switch (language) {
            case "Tamil" -> "அப்ரிகாட் (சர்க்கரை பாதாமி) என்பது பீச், பிளம் போன்ற கொட்டை கொண்ட சிறிய பழம். பழுத்தபோது மஞ்சள்-ஆரஞ்சு நிறமாகவும், இனிப்பு மற்றும் சிறிய புளிப்புச் சுவையுடனும் இருக்கும். உலர்த்தியும் சாப்பிடுவர். இந்தியாவில் இது குளிர் மலைப் பகுதிகளில் வளர்கிறது; உங்கள் பகுதியில் வளருமா என்பது குளிர்கால வெப்பநிலை மற்றும் வகையைப் பொறுத்தது. Gemini AI அமைக்கப்படாததால் இது சுருக்கமான பொதுத் தகவல்.";
            case "Hindi" -> "खुबानी (एप्रिकॉट) आड़ू और बेर जैसा गुठली वाला छोटा फल है। पकने पर यह पीला-नारंगी, मीठा और थोड़ा खट्टा होता है; इसे ताज़ा या सुखाकर खाया जाता है। भारत में इसकी खेती मुख्यतः ठंडे पहाड़ी क्षेत्रों में होती है। आपके क्षेत्र में उपयुक्तता सर्दियों की ठंड और किस्म पर निर्भर करती है। Gemini AI कॉन्फ़िगर नहीं है, इसलिए यह सीमित सामान्य जानकारी है।";
            case "Malayalam" -> "ആപ്രിക്കോട്ട് പീച്ച്, പ്ലം എന്നിവയെപ്പോലെ കുരുവുള്ള ചെറിയ പഴമാണ്. പഴുക്കുമ്പോൾ മഞ്ഞ-ഓറഞ്ച് നിറവും മധുരത്തോടൊപ്പം നേരിയ പുളിപ്പും ഉണ്ടാകും; ഉണക്കിയും കഴിക്കുന്നു. ഇന്ത്യയിൽ തണുപ്പുള്ള മലമ്പ്രദേശങ്ങളിലാണ് പ്രധാനമായും വളർത്തുന്നത്. നിങ്ങളുടെ സ്ഥലത്തിന് അനുയോജ്യമാണോ എന്നത് ശൈത്യകാല തണുപ്പിനെയും ഇനത്തെയും ആശ്രയിക്കുന്നു. Gemini AI ക്രമീകരിച്ചിട്ടില്ല; ഇത് പരിമിതമായ പൊതുവിവരമാണ്.";
            case "Kannada" -> "ಏಪ್ರಿಕಾಟ್ ಪೀಚ್ ಮತ್ತು ಪ್ಲಮ್‌ನಂತೆ ಗಟ್ಟಿಯಾದ ಬೀಜವಿರುವ ಸಣ್ಣ ಹಣ್ಣು. ಹಣ್ಣಾದಾಗ ಹಳದಿ-ಕಿತ್ತಳೆ ಬಣ್ಣ ಹೊಂದಿದ್ದು ಸಿಹಿ ಮತ್ತು ಸ್ವಲ್ಪ ಹುಳಿ ರುಚಿಯಿರುತ್ತದೆ; ಒಣಗಿಸಿಯೂ ತಿನ್ನುತ್ತಾರೆ. ಭಾರತದಲ್ಲಿ ಇದನ್ನು ಮುಖ್ಯವಾಗಿ ತಂಪಾದ ಬೆಟ್ಟ ಪ್ರದೇಶಗಳಲ್ಲಿ ಬೆಳೆಯುತ್ತಾರೆ. ನಿಮ್ಮ ಪ್ರದೇಶಕ್ಕೆ ಸೂಕ್ತವೇ ಎಂಬುದು ಚಳಿಗಾಲದ ತಾಪಮಾನ ಮತ್ತು ತಳಿಯನ್ನು ಅವಲಂಬಿಸುತ್ತದೆ. Gemini AI ಹೊಂದಿಸಿಲ್ಲ; ಇದು ಸೀಮಿತ ಸಾಮಾನ್ಯ ಮಾಹಿತಿ.";
            case "Telugu" -> "ఆప్రికాట్ అనేది పీచ్, ప్లమ్‌లా గట్టి గింజ ఉండే చిన్న పండు. పండినప్పుడు పసుపు-నారింజ రంగులో, తీపి మరియు కొద్దిగా పులుపుతో ఉంటుంది; ఎండబెట్టి కూడా తింటారు. భారతదేశంలో చల్లని కొండ ప్రాంతాల్లో ఎక్కువగా పండిస్తారు. మీ ప్రాంతానికి సరిపోతుందా అనేది శీతాకాల చలి, రకంపై ఆధారపడి ఉంటుంది. Gemini AI అమర్చలేదు; ఇది పరిమిత సాధారణ సమాచారం.";
            case "Marathi" -> "जर्दाळू (अप्रिकॉट) हे पीच आणि प्लमसारखे बी असलेले छोटे फळ आहे. पिकल्यावर ते पिवळे-नारिंगी, गोड आणि किंचित आंबट असते; ते वाळवूनही खातात. भारतात त्याची लागवड प्रामुख्याने थंड डोंगराळ भागात होते. तुमच्या भागासाठी योग्य आहे का हे हिवाळ्यातील थंडी आणि जातीवर अवलंबून असते. Gemini AI कॉन्फिगर नाही; ही मर्यादित सामान्य माहिती आहे.";
            default -> "An apricot is a small stone fruit related to peaches and plums. When ripe, it is usually yellow-orange with sweet, slightly tart flesh. People eat it fresh or dried. In India it is mainly grown in cooler mountain regions; whether it suits your area depends on winter chill and variety. Gemini AI is not configured, so this is a short general definition, not an AI-generated growing recommendation.";
        };
    }

    private String coconutDefinition(String language) {
        return switch (language) {
            case "Tamil" -> "தேங்காய் என்பது தென்னை மரத்தின் பழம். அதன் வெளிப்புற நார், கடின ஓடு, உள்ளே வெள்ளை கரு மற்றும் தேங்காய் நீர் ஆகியவை பயன்படுகின்றன. Gemini AI அமைக்கப்படாததால் இது சுருக்கமான பொதுத் தகவல்.";
            case "Hindi" -> "नारियल, नारियल के पेड़ का फल है। इसमें रेशेदार बाहरी परत, कठोर खोल, सफेद गूदा और पानी होता है। Gemini AI कॉन्फ़िगर नहीं है; यह सीमित सामान्य जानकारी है।";
            case "Malayalam" -> "തെങ്ങിന്റെ കായാണ് തേങ്ങ. ഇതിന് നാരുള്ള പുറംതൊലി, കട്ടിയുള്ള ചിരട്ട, വെളുത്ത കാമ്പ്, തേങ്ങാവെള്ളം എന്നിവയുണ്ട്. Gemini AI ക്രമീകരിച്ചിട്ടില്ല; ഇത് പരിമിതമായ പൊതുവിവരമാണ്.";
            case "Kannada" -> "ತೆಂಗಿನಕಾಯಿ ತೆಂಗಿನ ಮರದ ಹಣ್ಣು. ಇದರಲ್ಲಿ ನಾರಿನ ಹೊರಪದರ, ಗಟ್ಟಿಯಾದ ಚಿಪ್ಪು, ಬಿಳಿ ತಿರುಳು ಮತ್ತು ನೀರು ಇರುತ್ತವೆ. Gemini AI ಹೊಂದಿಸಿಲ್ಲ; ಇದು ಸೀಮಿತ ಸಾಮಾನ್ಯ ಮಾಹಿತಿ.";
            case "Telugu" -> "కొబ్బరికాయ కొబ్బరి చెట్టు పండు. దీనికి పీచుతో కూడిన బయటి పొర, గట్టి చిప్ప, తెల్లని గుజ్జు, కొబ్బరి నీరు ఉంటాయి. Gemini AI అమర్చలేదు; ఇది పరిమిత సాధారణ సమాచారం.";
            case "Marathi" -> "नारळ हे नारळाच्या झाडाचे फळ आहे. त्याला तंतुमय बाहेरील आवरण, कठीण करवंटी, पांढरा गर आणि नारळपाणी असते. Gemini AI कॉन्फिगर नाही; ही मर्यादित सामान्य माहिती आहे.";
            default -> "A coconut is the fruit of the coconut palm. It has a fibrous outer husk, a hard shell, white flesh and coconut water. Gemini AI is not configured, so this is a short general definition.";
        };
    }

    private String coconutChecklist(String language) {
        return switch (language) {
            case "Tamil" -> "Gemini AI அமைக்கப்படவில்லை; இது பொதுவான தென்னை பராமரிப்பு வழிகாட்டி. மரத்தைச் சுற்றி களைகளை அகற்றி, நீர் தேங்காமல் மண்ணின் ஈரத்தைச் சீராக வைத்திருங்கள்; உலர்ந்த இலைகளை மூடாக்காகப் பயன்படுத்தலாம். குருத்து மற்றும் இலைகளைத் தொடர்ந்து கவனியுங்கள். உரம் அல்லது பூச்சிமருந்தை மண் பரிசோதனை மற்றும் உள்ளூர் வேளாண் பரிந்துரைப்படி மட்டுமே பயன்படுத்துங்கள். மரத்தின் வயது, மண் வகை, நீர்ப்பாசனம், அறிகுறிகள் என்ன?";
            case "Hindi" -> "Gemini AI कॉन्फ़िगर नहीं है; यह नारियल की सामान्य देखभाल सूची है। तने के आसपास खरपतवार हटाएँ और जलभराव से बचाते हुए मिट्टी में नमी बनाए रखें; सूखी पत्तियों से मल्च कर सकते हैं। मुकुट और पत्तियों को नियमित देखें। खाद या कीटनाशक केवल मिट्टी-जाँच और स्थानीय कृषि सलाह के अनुसार दें। पेड़ की उम्र, मिट्टी, सिंचाई और लक्षण क्या हैं?";
            case "Malayalam" -> "Gemini AI ക്രമീകരിച്ചിട്ടില്ല; തെങ്ങിനുള്ള പൊതുവായ പരിപാലന നിർദ്ദേശമാണിത്. തെങ്ങിൻ തടത്തിലെ കളകൾ നീക്കി വെള്ളക്കെട്ടില്ലാതെ മണ്ണിൽ ഈർപ്പം നിലനിർത്തുക; ഉണങ്ങിയ ഇലകൾ പുതയിടാം. മണ്ടയും ഇലകളും പതിവായി പരിശോധിക്കുക. മണ്ണുപരിശോധനയും പ്രാദേശിക കൃഷി നിർദ്ദേശവും അനുസരിച്ച് മാത്രം വളമോ കീടനാശിനിയോ ഉപയോഗിക്കുക. തെങ്ങിന്റെ പ്രായം, മണ്ണ്, ജലസേചനം, ലക്ഷണങ്ങൾ എന്തൊക്കെയാണ്?";
            case "Kannada" -> "Gemini AI ಹೊಂದಿಸಿಲ್ಲ; ಇದು ತೆಂಗಿನ ಮರದ ಸಾಮಾನ್ಯ ಆರೈಕೆ ಮಾಹಿತಿ. ಮರದ ಸುತ್ತಲಿನ ಕಳೆ ತೆಗೆಯಿರಿ; ನೀರು ನಿಲ್ಲದಂತೆ ಮಣ್ಣಿನ ತೇವ ಉಳಿಸಿ ಮತ್ತು ಒಣ ಎಲೆಗಳನ್ನು ಹೊದಿಕೆಯಾಗಿ ಬಳಸಿ. ಗರಿ ಮತ್ತು ಎಲೆಗಳನ್ನು ನಿಯಮಿತವಾಗಿ ಪರಿಶೀಲಿಸಿ. ಮಣ್ಣಿನ ಪರೀಕ್ಷೆ ಹಾಗೂ ಸ್ಥಳೀಯ ಕೃಷಿ ಸಲಹೆಯಂತೆ ಮಾತ್ರ ಗೊಬ್ಬರ ಅಥವಾ ಕೀಟನಾಶಕ ಬಳಸಿ. ಮರದ ವಯಸ್ಸು, ಮಣ್ಣು, ನೀರಾವರಿ ಮತ್ತು ಲಕ್ಷಣಗಳೇನು?";
            case "Telugu" -> "Gemini AI అమర్చలేదు; ఇది కొబ్బరి చెట్టుకు సాధారణ సంరక్షణ సూచన. చెట్టు చుట్టూ కలుపు తొలగించి, నీరు నిల్వ కాకుండా నేల తేమను ఉంచండి; ఎండిన ఆకులను మల్చ్‌గా వాడవచ్చు. కిరీటం, ఆకులను తరచూ పరిశీలించండి. నేల పరీక్ష, స్థానిక వ్యవసాయ సూచన ప్రకారం మాత్రమే ఎరువు లేదా పురుగుమందు వాడండి. చెట్టు వయస్సు, నేల, నీటిపారుదల, లక్షణాలు ఏమిటి?";
            case "Marathi" -> "Gemini AI कॉन्फिगर केलेले नाही; ही नारळाच्या झाडासाठी सामान्य काळजीसूची आहे. झाडाभोवतीचे तण काढा; पाणी साचू न देता जमिनीत ओलावा ठेवा आणि वाळलेली पाने आच्छादनासाठी वापरा. शेंडा व पाने नियमित तपासा. माती परीक्षण आणि स्थानिक कृषी सल्ल्यानुसारच खत किंवा कीटकनाशक वापरा. झाडाचे वय, माती, पाणी आणि लक्षणे कोणती?";
            default -> "Gemini AI is not configured; this is general coconut-care guidance. Keep the basin weed-free and the soil evenly moist without waterlogging; dry leaves can be used as mulch. Check the crown and leaves regularly. Use fertilizer or pesticides only according to a soil test and local agriculture advice. What is the palm's age, soil type, irrigation, and any symptoms?";
        };
    }
}
