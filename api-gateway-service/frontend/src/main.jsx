import React, { useEffect, useRef, useState } from "react";
import { createRoot } from "react-dom/client";

const languageCopy = {
  English: {
    eyebrow: "Photo-based plant check",
    title: "Check a leaf or stem",
    intro: "Take a clear photo of an affected plant part to get an AI estimate and safe next steps.",
    visionSetup: "Photo AI needs GOOGLE_API_KEY configured for the Advisory service. Camera capture and upload work, but AI analysis is unavailable until the key is set.",
    photoTitle: "Take or choose a plant photo",
    crop: "Crop",
    cropPlaceholder: "e.g. tomato, rice",
    district: "District (optional)",
    language: "Answer language",
    choosePhoto: "📷 Take / choose photo",
    openCamera: "Open camera",
    capture: "Capture photo",
    closeCamera: "Close camera",
    photoTip: "Use a well-lit photo of one affected leaf or stem. JPEG or PNG, up to 8 MB.",
    privacy: "The photo is sent to Google Gemini for analysis; Uzhavam saves the assessment, not the original photo.",
    analyze: "Analyze photo",
    analyzing: "Checking the photo…",
    resultTitle: "Photo assessment",
    historyTitle: "Recent plant checks",
    historyTip: "Assessment details are saved; the original photo is not stored.",
    refresh: "Refresh",
    delete: "Delete",
    deleteConfirm: "Remove this saved plant check?",
    deleted: "Plant check removed from your records.",
    loadingHistory: "Loading saved checks…",
    emptyHistory: "No plant checks saved yet.",
    notConfirmed: "This is an AI estimate from one photo, not a confirmed diagnosis. Do not apply chemicals based only on this result; confirm with a local agriculture officer or KVK.",
    cropFound: "Crop identified",
    likelyIssue: "Possible issue",
    confidence: "Model confidence",
    severity: "Visible severity",
    observations: "What is visible",
    recommendations: "Safer next steps",
    source: "Reference",
    cameraDenied: "Camera could not be opened. Check browser camera permission or choose a photo instead.",
    fileError: "Choose a JPEG or PNG photo smaller than 8 MB.",
    noCamera: "Camera access is not available here. Choose a photo from your device instead.",
    needCrop: "Enter the crop name first.",
    requestError: "Photo analysis failed.",
    historyError: "Could not load saved plant checks.",
    saved: "Assessment saved to your farm records.",
    noResult: "No assessment was returned.",
    savedAt: "Saved",
    low: "Low",
    moderate: "Moderate",
    high: "High",
    unknown: "Unknown"
  },
  Tamil: {
    eyebrow: "படத்தின் மூலம் பயிர் பரிசோதனை", title: "இலை அல்லது தண்டைப் பரிசோதிக்கவும்",
    intro: "பாதிக்கப்பட்ட பகுதியின் தெளிவான படத்தை எடுத்து AI மதிப்பீடும் பாதுகாப்பான அடுத்த படிகளும் பெறுங்கள்.",
    visionSetup: "பட AI ஆய்வுக்கு Advisory சேவையில் GOOGLE_API_KEY அமைக்க வேண்டும். படம் எடுக்கவும் பதிவேற்றவும் முடியும்; விசை அமைக்கும் வரை AI ஆய்வு கிடைக்காது.",
    photoTitle: "பயிர்ப் படம் எடுக்கவும் அல்லது தேர்வு செய்யவும்", crop: "பயிர்", cropPlaceholder: "உதா: தக்காளி, நெல்",
    district: "மாவட்டம் (விருப்பம்)", language: "பதில் மொழி", choosePhoto: "📷 படம் எடுக்க / தேர்வு செய்ய",
    openCamera: "கேமராவைத் திற", capture: "படம் எடுக்க", closeCamera: "கேமராவை மூட",
    photoTip: "பாதிக்கப்பட்ட ஒரு இலை அல்லது தண்டின் தெளிவான, வெளிச்சமான படத்தைப் பயன்படுத்துங்கள். JPEG அல்லது PNG, 8 MB வரை.",
    privacy: "படம் ஆய்வுக்காக Google Gemini-க்கு அனுப்பப்படும்; மதிப்பீடு மட்டும் சேமிக்கப்படும், அசல் படம் சேமிக்கப்படாது.",
    analyze: "படத்தைப் பரிசோதிக்க", analyzing: "படத்தைப் பரிசோதிக்கிறது…", resultTitle: "பட மதிப்பீடு",
    historyTitle: "சமீபத்திய பயிர் பரிசோதனைகள்", historyTip: "மதிப்பீடு சேமிக்கப்படும்; அசல் படம் சேமிக்கப்படாது.",
    refresh: "புதுப்பிக்க", loadingHistory: "சேமித்த பரிசோதனைகளை ஏற்றுகிறது…", emptyHistory: "பயிர் பரிசோதனைகள் இன்னும் இல்லை.",
    delete: "நீக்கு", deleteConfirm: "இந்தச் சேமித்த பரிசோதனையை நீக்கவா?", deleted: "பயிர் பரிசோதனை நீக்கப்பட்டது.",
    notConfirmed: "இது ஒரு படத்தின் அடிப்படையிலான AI மதிப்பீடு மட்டுமே; உறுதியான நோயறிதல் அல்ல. இதை மட்டும் நம்பி மருந்து பயன்படுத்த வேண்டாம். வேளாண் அலுவலர் அல்லது KVK-யிடம் உறுதிப்படுத்தவும்.",
    cropFound: "கண்டறிந்த பயிர்", likelyIssue: "சாத்தியமான பாதிப்பு", confidence: "AI நம்பிக்கை அளவு", severity: "காணும் பாதிப்பு அளவு",
    observations: "படத்தில் காண்பது", recommendations: "பாதுகாப்பான அடுத்த படிகள்", source: "ஆதாரம்",
    cameraDenied: "கேமராவைத் திறக்க முடியவில்லை. அனுமதியைச் சரிபார்க்கவும் அல்லது படத்தைத் தேர்வு செய்யவும்.",
    fileError: "8 MB-க்குள் JPEG அல்லது PNG படத்தைத் தேர்வு செய்யவும்.", noCamera: "கேமரா கிடைக்கவில்லை. சாதனத்திலிருந்து படத்தைத் தேர்வு செய்யவும்.",
    needCrop: "முதலில் பயிரின் பெயரை உள்ளிடவும்.", requestError: "படப் பரிசோதனை தோல்வியடைந்தது.",
    historyError: "சேமித்த பரிசோதனைகளை ஏற்ற முடியவில்லை.", saved: "மதிப்பீடு பண்ணைப் பதிவுகளில் சேமிக்கப்பட்டது.",
    noResult: "மதிப்பீடு கிடைக்கவில்லை.", savedAt: "சேமித்த நேரம்", low: "குறைவு", moderate: "மிதமான", high: "அதிகம்", unknown: "தெரியவில்லை"
  },
  Hindi: {
    eyebrow: "तस्वीर से पौधे की जाँच", title: "पत्ती या तने की जाँच करें",
    intro: "प्रभावित हिस्से की साफ़ तस्वीर लें और AI का अनुमान तथा सुरक्षित अगले कदम पाएँ।",
    visionSetup: "फोटो AI जाँच के लिए Advisory सेवा में GOOGLE_API_KEY सेट करना होगा। कैमरे से फोटो लेना और अपलोड करना काम करता है, लेकिन कुंजी सेट होने तक AI जाँच उपलब्ध नहीं है।",
    photoTitle: "पौधे की तस्वीर लें या चुनें", crop: "फसल", cropPlaceholder: "जैसे टमाटर, धान",
    district: "ज़िला (वैकल्पिक)", language: "जवाब की भाषा", choosePhoto: "📷 तस्वीर लें / चुनें",
    openCamera: "कैमरा खोलें", capture: "तस्वीर लें", closeCamera: "कैमरा बंद करें",
    photoTip: "प्रभावित पत्ती या तने की साफ़, रोशनी वाली तस्वीर दें। JPEG या PNG, 8 MB तक।",
    privacy: "तस्वीर जाँच के लिए Google Gemini को भेजी जाती है; Uzhavam आकलन सहेजता है, मूल तस्वीर नहीं।",
    analyze: "तस्वीर जाँचें", analyzing: "तस्वीर की जाँच हो रही है…", resultTitle: "तस्वीर का आकलन",
    historyTitle: "हाल की पौधा जाँच", historyTip: "आकलन सहेजा जाता है; मूल तस्वीर नहीं।",
    refresh: "रीफ़्रेश", loadingHistory: "सहेजी जाँच लोड हो रही है…", emptyHistory: "अभी कोई जाँच सहेजी नहीं गई।",
    delete: "हटाएँ", deleteConfirm: "क्या यह सहेजी जाँच हटाएँ?", deleted: "पौधा जाँच हटा दी गई।",
    notConfirmed: "यह एक तस्वीर पर आधारित AI अनुमान है, पक्का निदान नहीं। केवल इस नतीजे के आधार पर रसायन न डालें; स्थानीय कृषि अधिकारी या KVK से पुष्टि करें।",
    cropFound: "पहचानी गई फसल", likelyIssue: "संभावित समस्या", confidence: "मॉडल का भरोसा", severity: "दिखने वाली गंभीरता",
    observations: "तस्वीर में क्या दिखता है", recommendations: "सुरक्षित अगले कदम", source: "स्रोत",
    cameraDenied: "कैमरा नहीं खुला। ब्राउज़र अनुमति जाँचें या तस्वीर चुनें।", fileError: "8 MB से छोटी JPEG या PNG तस्वीर चुनें।",
    noCamera: "कैमरा उपलब्ध नहीं है। अपने उपकरण से तस्वीर चुनें।", needCrop: "पहले फसल का नाम भरें।",
    requestError: "तस्वीर की जाँच विफल हुई।", historyError: "सहेजी गई जाँच लोड नहीं हो सकी।",
    saved: "आकलन आपके खेत के रिकॉर्ड में सहेजा गया।", noResult: "कोई आकलन नहीं मिला।",
    savedAt: "सहेजा गया", low: "कम", moderate: "मध्यम", high: "ज़्यादा", unknown: "अज्ञात"
  },
  Malayalam: {
    eyebrow: "ചിത്രം ഉപയോഗിച്ചുള്ള ചെടി പരിശോധന", title: "ഇലയോ തണ്ടോ പരിശോധിക്കുക",
    intro: "രോഗം ബാധിച്ച ഭാഗത്തിന്റെ വ്യക്തമായ ചിത്രം എടുത്ത് AI വിലയിരുത്തലും സുരക്ഷിതമായ തുടർനടപടികളും നേടുക.",
    visionSetup: "ഫോട്ടോ AI പരിശോധനയ്ക്ക് Advisory സേവനത്തിൽ GOOGLE_API_KEY സജ്ജമാക്കണം. ക്യാമറയിൽ ചിത്രം എടുക്കാനും അപ്‌ലോഡ് ചെയ്യാനും കഴിയും; കീ സജ്ജമാകുന്നതുവരെ AI പരിശോധന ലഭ്യമല്ല.",
    photoTitle: "ചെടിയുടെ ചിത്രം എടുക്കുക അല്ലെങ്കിൽ തിരഞ്ഞെടുക്കുക", crop: "വിള", cropPlaceholder: "ഉദാ: തക്കാളി, നെല്ല്",
    district: "ജില്ല (ഐച്ഛികം)", language: "മറുപടി ഭാഷ", choosePhoto: "📷 ചിത്രം എടുക്കുക / തിരഞ്ഞെടുക്കുക",
    openCamera: "ക്യാമറ തുറക്കുക", capture: "ചിത്രം എടുക്കുക", closeCamera: "ക്യാമറ അടയ്ക്കുക",
    photoTip: "വെളിച്ചമുള്ള സ്ഥലത്ത് ബാധിച്ച ഇലയുടെയോ തണ്ടിന്റെയോ വ്യക്തമായ ചിത്രം നൽകുക. JPEG അല്ലെങ്കിൽ PNG, 8 MB വരെ.",
    privacy: "ചിത്രം വിശകലനത്തിനായി Google Gemini-ലേക്ക് അയയ്ക്കുന്നു; വിലയിരുത്തൽ മാത്രമാണ് Uzhavam സേവ് ചെയ്യുന്നത്, യഥാർത്ഥ ചിത്രം അല്ല.",
    analyze: "ചിത്രം പരിശോധിക്കുക", analyzing: "ചിത്രം പരിശോധിക്കുന്നു…", resultTitle: "ചിത്ര വിലയിരുത്തൽ",
    historyTitle: "സമീപകാല ചെടി പരിശോധനകൾ", historyTip: "വിലയിരുത്തൽ സേവ് ചെയ്യും; യഥാർത്ഥ ചിത്രം സേവ് ചെയ്യില്ല.",
    refresh: "പുതുക്കുക", loadingHistory: "സേവ് ചെയ്ത പരിശോധനകൾ ലോഡ് ചെയ്യുന്നു…", emptyHistory: "ഇതുവരെ പരിശോധനകളൊന്നും സേവ് ചെയ്തിട്ടില്ല.",
    delete: "നീക്കുക", deleteConfirm: "ഈ സേവ് ചെയ്ത പരിശോധന നീക്കണോ?", deleted: "ചെടി പരിശോധന നീക്കി.",
    notConfirmed: "ഇത് ഒരു ചിത്രത്തെ അടിസ്ഥാനമാക്കിയുള്ള AI ഏകദേശ വിലയിരുത്തലാണ്; ഉറപ്പായ രോഗനിർണയമല്ല. ഇതിന്റെ മാത്രം അടിസ്ഥാനത്തിൽ രാസവസ്തുക്കൾ ഉപയോഗിക്കരുത്; പ്രാദേശിക കൃഷി ഓഫീസറോടോ KVK-യോടോ ഉറപ്പാക്കുക.",
    cropFound: "തിരിച്ചറിഞ്ഞ വിള", likelyIssue: "സാധ്യമായ പ്രശ്നം", confidence: "മോഡൽ വിശ്വാസനില", severity: "കാണുന്ന തീവ്രത",
    observations: "ചിത്രത്തിൽ കാണുന്നത്", recommendations: "സുരക്ഷിതമായ അടുത്ത നടപടികൾ", source: "അവലംബം",
    cameraDenied: "ക്യാമറ തുറക്കാനായില്ല. അനുമതി പരിശോധിക്കുക അല്ലെങ്കിൽ ചിത്രം തിരഞ്ഞെടുക്കുക.",
    fileError: "8 MB-ൽ താഴെയുള്ള JPEG അല്ലെങ്കിൽ PNG ചിത്രം തിരഞ്ഞെടുക്കുക.", noCamera: "ക്യാമറ ലഭ്യമല്ല. ഉപകരണത്തിൽ നിന്ന് ചിത്രം തിരഞ്ഞെടുക്കുക.",
    needCrop: "ആദ്യം വിളയുടെ പേര് നൽകുക.", requestError: "ചിത്ര പരിശോധന പരാജയപ്പെട്ടു.",
    historyError: "സേവ് ചെയ്ത പരിശോധനകൾ ലോഡ് ചെയ്യാനായില്ല.", saved: "വിലയിരുത്തൽ കൃഷിയിട രേഖകളിൽ സേവ് ചെയ്തു.",
    noResult: "വിലയിരുത്തൽ ലഭിച്ചില്ല.", savedAt: "സേവ് ചെയ്തത്", low: "കുറവ്", moderate: "മിതമായ", high: "കൂടുതൽ", unknown: "അറിയില്ല"
  },
  Kannada: {
    eyebrow: "ಚಿತ್ರದ ಮೂಲಕ ಸಸ್ಯ ತಪಾಸಣೆ", title: "ಎಲೆ ಅಥವಾ ಕಾಂಡವನ್ನು ಪರಿಶೀಲಿಸಿ",
    intro: "ಬಾಧಿತ ಭಾಗದ ಸ್ಪಷ್ಟ ಚಿತ್ರ ತೆಗೆದು AI ಅಂದಾಜು ಮತ್ತು ಸುರಕ್ಷಿತ ಮುಂದಿನ ಕ್ರಮಗಳನ್ನು ಪಡೆಯಿರಿ.",
    visionSetup: "ಫೋಟೋ AI ಪರಿಶೀಲನೆಗೆ Advisory ಸೇವೆಯಲ್ಲಿ GOOGLE_API_KEY ಹೊಂದಿಸಬೇಕು. ಕ್ಯಾಮೆರಾ ಚಿತ್ರ ಮತ್ತು ಅಪ್‌ಲೋಡ್ ಕೆಲಸ ಮಾಡುತ್ತವೆ; ಕೀ ಹೊಂದಿಸುವವರೆಗೆ AI ಪರಿಶೀಲನೆ ಲಭ್ಯವಿಲ್ಲ.",
    photoTitle: "ಸಸ್ಯದ ಚಿತ್ರ ತೆಗೆಯಿರಿ ಅಥವಾ ಆಯ್ಕೆಮಾಡಿ", crop: "ಬೆಳೆ", cropPlaceholder: "ಉದಾ: ಟೊಮೇಟೊ, ಭತ್ತ",
    district: "ಜಿಲ್ಲೆ (ಐಚ್ಛಿಕ)", language: "ಉತ್ತರದ ಭಾಷೆ", choosePhoto: "📷 ಚಿತ್ರ ತೆಗೆಯಿರಿ / ಆಯ್ಕೆಮಾಡಿ",
    openCamera: "ಕ್ಯಾಮೆರಾ ತೆರೆಯಿರಿ", capture: "ಚಿತ್ರ ಸೆರೆಹಿಡಿಯಿರಿ", closeCamera: "ಕ್ಯಾಮೆರಾ ಮುಚ್ಚಿ",
    photoTip: "ಬಾಧಿತ ಎಲೆ ಅಥವಾ ಕಾಂಡದ ಸ್ಪಷ್ಟ, ಬೆಳಕಿರುವ ಚಿತ್ರ ಬಳಸಿ. JPEG ಅಥವಾ PNG, 8 MB ವರೆಗೆ.",
    privacy: "ಚಿತ್ರವನ್ನು ವಿಶ್ಲೇಷಣೆಗಾಗಿ Google Geminiಗೆ ಕಳುಹಿಸಲಾಗುತ್ತದೆ; Uzhavam ಅಂದಾಜನ್ನು ಮಾತ್ರ ಉಳಿಸುತ್ತದೆ, ಮೂಲ ಚಿತ್ರವನ್ನಲ್ಲ.",
    analyze: "ಚಿತ್ರ ಪರಿಶೀಲಿಸಿ", analyzing: "ಚಿತ್ರ ಪರಿಶೀಲಿಸಲಾಗುತ್ತಿದೆ…", resultTitle: "ಚಿತ್ರದ ಅಂದಾಜು",
    historyTitle: "ಇತ್ತೀಚಿನ ಸಸ್ಯ ತಪಾಸಣೆಗಳು", historyTip: "ಅಂದಾಜು ಉಳಿಯುತ್ತದೆ; ಮೂಲ ಚಿತ್ರ ಉಳಿಸಲಾಗುವುದಿಲ್ಲ.",
    refresh: "ಮರುಲೋಡ್", loadingHistory: "ಉಳಿಸಿದ ತಪಾಸಣೆಗಳನ್ನು ತೆರೆಯಲಾಗುತ್ತಿದೆ…", emptyHistory: "ಇನ್ನೂ ತಪಾಸಣೆಗಳನ್ನು ಉಳಿಸಿಲ್ಲ.",
    delete: "ಅಳಿಸಿ", deleteConfirm: "ಈ ಉಳಿಸಿದ ತಪಾಸಣೆಯನ್ನು ಅಳಿಸಬೇಕೇ?", deleted: "ಸಸ್ಯ ತಪಾಸಣೆಯನ್ನು ಅಳಿಸಲಾಗಿದೆ.",
    notConfirmed: "ಇದು ಒಂದು ಚಿತ್ರದಿಂದ ಸಿಗುವ AI ಅಂದಾಜು ಮಾತ್ರ; ಖಚಿತ ರೋಗನಿರ್ಣಯವಲ್ಲ. ಇದನ್ನೇ ಆಧರಿಸಿ ರಾಸಾಯನಿಕ ಬಳಸದಿರಿ; ಸ್ಥಳೀಯ ಕೃಷಿ ಅಧಿಕಾರಿ ಅಥವಾ KVK ಬಳಿ ದೃಢಪಡಿಸಿ.",
    cropFound: "ಗುರುತಿಸಿದ ಬೆಳೆ", likelyIssue: "ಸಂಭಾವ್ಯ ಸಮಸ್ಯೆ", confidence: "ಮಾದರಿಯ ವಿಶ್ವಾಸ", severity: "ಕಾಣುವ ತೀವ್ರತೆ",
    observations: "ಚಿತ್ರದಲ್ಲಿ ಕಾಣುವುದು", recommendations: "ಸುರಕ್ಷಿತ ಮುಂದಿನ ಕ್ರಮಗಳು", source: "ಆಧಾರ",
    cameraDenied: "ಕ್ಯಾಮೆರಾ ತೆರೆಯಲಿಲ್ಲ. ಅನುಮತಿ ಪರಿಶೀಲಿಸಿ ಅಥವಾ ಚಿತ್ರ ಆಯ್ಕೆಮಾಡಿ.",
    fileError: "8 MB ಒಳಗಿನ JPEG ಅಥವಾ PNG ಚಿತ್ರ ಆಯ್ಕೆಮಾಡಿ.", noCamera: "ಕ್ಯಾಮೆರಾ ಲಭ್ಯವಿಲ್ಲ. ಸಾಧನದಿಂದ ಚಿತ್ರ ಆಯ್ಕೆಮಾಡಿ.",
    needCrop: "ಮೊದಲು ಬೆಳೆಯ ಹೆಸರನ್ನು ನಮೂದಿಸಿ.", requestError: "ಚಿತ್ರ ಪರಿಶೀಲನೆ ವಿಫಲವಾಗಿದೆ.",
    historyError: "ಉಳಿಸಿದ ತಪಾಸಣೆಗಳನ್ನು ಲೋಡ್ ಮಾಡಲಾಗಲಿಲ್ಲ.", saved: "ಅಂದಾಜನ್ನು ನಿಮ್ಮ ಹೊಲದ ದಾಖಲೆಗಳಲ್ಲಿ ಉಳಿಸಲಾಗಿದೆ.",
    noResult: "ಅಂದಾಜು ದೊರೆಯಲಿಲ್ಲ.", savedAt: "ಉಳಿಸಿದ ಸಮಯ", low: "ಕಡಿಮೆ", moderate: "ಮಧ್ಯಮ", high: "ಹೆಚ್ಚು", unknown: "ತಿಳಿದಿಲ್ಲ"
  },
  Telugu: {
    eyebrow: "ఫోటోతో మొక్క పరిశీలన", title: "ఆకు లేదా కాండాన్ని పరిశీలించండి",
    intro: "దెబ్బతిన్న భాగం స్పష్టమైన ఫోటో తీసి AI అంచనా, సురక్షితమైన తదుపరి సూచనలు పొందండి.",
    visionSetup: "ఫోటో AI పరీక్షకు Advisory సేవలో GOOGLE_API_KEY సెట్ చేయాలి. కెమెరాతో ఫోటో తీయడం, అప్‌లోడ్ చేయడం పనిచేస్తాయి; కీ సెట్ చేసే వరకు AI పరీక్ష అందుబాటులో ఉండదు.",
    photoTitle: "మొక్క ఫోటో తీయండి లేదా ఎంచుకోండి", crop: "పంట", cropPlaceholder: "ఉదా: టమాటా, వరి",
    district: "జిల్లా (ఐచ్ఛికం)", language: "సమాధాన భాష", choosePhoto: "📷 ఫోటో తీయండి / ఎంచుకోండి",
    openCamera: "కెమెరా తెరవండి", capture: "ఫోటో తీయండి", closeCamera: "కెమెరా మూసివేయండి",
    photoTip: "దెబ్బతిన్న ఆకు లేదా కాండం స్పష్టంగా కనిపించే వెలుతురు ఉన్న ఫోటో ఇవ్వండి. JPEG లేదా PNG, 8 MB వరకు.",
    privacy: "ఫోటో విశ్లేషణ కోసం Google Geminiకి పంపబడుతుంది; Uzhavam అంచనాను మాత్రమే సేవ్ చేస్తుంది, అసలు ఫోటోను కాదు.",
    analyze: "ఫోటోను పరిశీలించండి", analyzing: "ఫోటోను పరిశీలిస్తోంది…", resultTitle: "ఫోటో అంచనా",
    historyTitle: "ఇటీవలి మొక్క పరిశీలనలు", historyTip: "అంచనా మాత్రమే సేవ్ అవుతుంది; అసలు ఫోటో సేవ్ కాదు.",
    refresh: "రిఫ్రెష్", loadingHistory: "సేవ్ చేసిన పరిశీలనలను తెరుస్తోంది…", emptyHistory: "ఇంకా మొక్క పరిశీలనలు లేవు.",
    delete: "తొలగించండి", deleteConfirm: "ఈ సేవ్ చేసిన పరిశీలనను తొలగించాలా?", deleted: "మొక్క పరిశీలన తొలగించబడింది.",
    notConfirmed: "ఇది ఒక ఫోటో ఆధారంగా AI ఇచ్చిన అంచనా మాత్రమే; నిర్ధారిత వ్యాధి నిర్ధారణ కాదు. దీని ఆధారంగా మాత్రమే రసాయనాలు వాడకండి; స్థానిక వ్యవసాయ అధికారి లేదా KVKతో నిర్ధారించండి.",
    cropFound: "గుర్తించిన పంట", likelyIssue: "సంభావ్య సమస్య", confidence: "మోడల్ విశ్వాసం", severity: "కనిపించే తీవ్రత",
    observations: "ఫోటోలో కనిపిస్తున్నది", recommendations: "సురక్షిత తదుపరి చర్యలు", source: "ఆధారం",
    cameraDenied: "కెమెరా తెరవలేకపోయాం. అనుమతులు చూడండి లేదా ఫోటో ఎంచుకోండి.",
    fileError: "8 MB కంటే తక్కువ JPEG లేదా PNG ఫోటో ఎంచుకోండి.", noCamera: "కెమెరా అందుబాటులో లేదు. పరికరం నుంచి ఫోటో ఎంచుకోండి.",
    needCrop: "ముందుగా పంట పేరు నమోదు చేయండి.", requestError: "ఫోటో పరిశీలన విఫలమైంది.",
    historyError: "సేవ్ చేసిన పరిశీలనలను లోడ్ చేయలేకపోయాం.", saved: "అంచనా మీ వ్యవసాయ రికార్డుల్లో సేవ్ అయింది.",
    noResult: "అంచనా రాలేదు.", savedAt: "సేవ్ చేసిన సమయం", low: "తక్కువ", moderate: "మధ్యస్థం", high: "ఎక్కువ", unknown: "తెలియదు"
  },
  Marathi: {
    eyebrow: "फोटोवरून रोपाची तपासणी", title: "पान किंवा खोड तपासा",
    intro: "बाधित भागाचा स्पष्ट फोटो काढा आणि AI अंदाजासह सुरक्षित पुढील सूचना मिळवा.",
    visionSetup: "फोटो AI तपासणीसाठी Advisory सेवेमध्ये GOOGLE_API_KEY सेट करणे आवश्यक आहे. कॅमेराने फोटो घेणे आणि अपलोड करणे चालते; की सेट होईपर्यंत AI तपासणी उपलब्ध नाही.",
    photoTitle: "रोपाचा फोटो काढा किंवा निवडा", crop: "पीक", cropPlaceholder: "उदा. टोमॅटो, भात",
    district: "जिल्हा (ऐच्छिक)", language: "उत्तराची भाषा", choosePhoto: "📷 फोटो काढा / निवडा",
    openCamera: "कॅमेरा उघडा", capture: "फोटो काढा", closeCamera: "कॅमेरा बंद करा",
    photoTip: "बाधित पान किंवा खोडाचा स्वच्छ, उजेडातील फोटो वापरा. JPEG किंवा PNG, 8 MB पर्यंत.",
    privacy: "फोटो तपासणीसाठी Google Gemini कडे पाठवला जातो; Uzhavam मूल्यांकन जतन करते, मूळ फोटो नाही.",
    analyze: "फोटो तपासा", analyzing: "फोटो तपासत आहे…", resultTitle: "फोटोचे मूल्यांकन",
    historyTitle: "अलीकडील रोप तपासण्या", historyTip: "मूल्यांकन जतन होते; मूळ फोटो जतन होत नाही.",
    refresh: "रिफ्रेश", loadingHistory: "जतन केलेल्या तपासण्या लोड होत आहेत…", emptyHistory: "अजून रोप तपासणी जतन केलेली नाही.",
    delete: "काढा", deleteConfirm: "ही जतन केलेली तपासणी काढायची?", deleted: "रोप तपासणी काढली.",
    notConfirmed: "हा एका फोटोवर आधारित AI अंदाज आहे, निश्चित निदान नाही. या निकालावरूनच रसायनांचा वापर करू नका; स्थानिक कृषी अधिकारी किंवा KVK कडून खात्री करा.",
    cropFound: "ओळखलेले पीक", likelyIssue: "संभाव्य समस्या", confidence: "मॉडेलचा विश्वास", severity: "दिसणारी तीव्रता",
    observations: "फोटोमध्ये काय दिसते", recommendations: "सुरक्षित पुढील पावले", source: "संदर्भ",
    cameraDenied: "कॅमेरा उघडता आला नाही. परवानगी तपासा किंवा फोटो निवडा.",
    fileError: "8 MB पेक्षा लहान JPEG किंवा PNG फोटो निवडा.", noCamera: "कॅमेरा उपलब्ध नाही. डिव्हाइसमधून फोटो निवडा.",
    needCrop: "आधी पिकाचे नाव भरा.", requestError: "फोटो तपासणी अयशस्वी झाली.",
    historyError: "जतन केलेल्या तपासण्या लोड करता आल्या नाहीत.", saved: "मूल्यांकन शेताच्या नोंदीत जतन केले.",
    noResult: "मूल्यांकन मिळाले नाही.", savedAt: "जतन केले", low: "कमी", moderate: "मध्यम", high: "जास्त", unknown: "अज्ञात"
  }
};

const languages = ["English", "Tamil", "Hindi", "Malayalam", "Kannada", "Telugu", "Marathi"];
const MAX_IMAGE_BYTES = 8 * 1024 * 1024;

function PlantHealthApp() {
  const [language, setLanguage] = useState(window.uzhavamGetLanguage?.() || "English");
  const [cropName, setCropName] = useState("");
  const [district, setDistrict] = useState("");
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState("");
  const [cameraOpen, setCameraOpen] = useState(false);
  const [cameraReady, setCameraReady] = useState(false);
  const [visionConfigured, setVisionConfigured] = useState(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);
  const [historyBusy, setHistoryBusy] = useState(false);
  const [authenticated, setAuthenticated] = useState(
    document.getElementById("loginShade")?.hidden === true
  );
  const fileInput = useRef(null);
  const video = useRef(null);
  const canvas = useRef(null);
  const stream = useRef(null);
  const previewUrl = useRef("");
  const text = languageCopy[language] || languageCopy.English;

  useEffect(() => {
    const login = () => setAuthenticated(true);
    const langChange = event => setLanguage(event.target.value);
    window.addEventListener("uzhavam:authenticated", login);
    const languageSelectors = ["siteLanguage", "chatLanguage", "profileLanguage"]
      .map(id => document.getElementById(id)).filter(Boolean);
    languageSelectors.forEach(selector => selector.addEventListener("change", langChange));
    return () => {
      window.removeEventListener("uzhavam:authenticated", login);
      languageSelectors.forEach(selector => selector.removeEventListener("change", langChange));
      stopCamera();
      clearPreview();
    };
  }, []);

  useEffect(() => {
    if (!authenticated) return;
    const profile = window.uzhavamGetProfile?.();
    if (profile?.district) setDistrict(profile.district);
    loadHistory();
    window.uzhavamApi("/api/advisory/vision/status")
      .then(status => setVisionConfigured(status.configured === true))
      .catch(cause => setError(`Could not check plant photo analysis setup. ${cause.message}`));
  }, [authenticated]);

  useEffect(() => {
    if (cameraOpen && video.current && stream.current) video.current.srcObject = stream.current;
  }, [cameraOpen]);

  function clearPreview() {
    if (previewUrl.current) URL.revokeObjectURL(previewUrl.current);
    previewUrl.current = "";
  }

  function stopCamera() {
    stream.current?.getTracks().forEach(track => track.stop());
    stream.current = null;
    setCameraReady(false);
    setCameraOpen(false);
  }

  function chooseFile(nextFile) {
    setError("");
    setNotice("");
    if (!nextFile) return;
    if (!["image/jpeg", "image/png"].includes(nextFile.type) || nextFile.size > MAX_IMAGE_BYTES) {
      setError(text.fileError);
      return;
    }
    stopCamera();
    clearPreview();
    const url = URL.createObjectURL(nextFile);
    previewUrl.current = url;
    setFile(nextFile);
    setPreview(url);
  }

  async function openCamera() {
    setError("");
    setCameraReady(false);
    if (!navigator.mediaDevices?.getUserMedia) {
      setError(text.noCamera);
      fileInput.current?.click();
      return;
    }
    try {
      stopCamera();
      stream.current = await navigator.mediaDevices.getUserMedia({
        audio: false,
        video: { facingMode: { ideal: "environment" }, width: { ideal: 1600 }, height: { ideal: 1200 } }
      });
      setCameraOpen(true);
    } catch (cause) {
      setError(text.cameraDenied);
    }
  }

  function capturePhoto() {
    if (!cameraReady || !video.current || !canvas.current) return;
    const source = video.current;
    const scale = Math.min(1, 1600 / Math.max(source.videoWidth, source.videoHeight));
    const target = canvas.current;
    target.width = Math.round(source.videoWidth * scale);
    target.height = Math.round(source.videoHeight * scale);
    target.getContext("2d").drawImage(source, 0, 0, target.width, target.height);
    target.toBlob(blob => {
      if (!blob) {
        setError(text.cameraDenied);
        return;
      }
      chooseFile(new File([blob], "uzhavam-plant-photo.jpg", { type: "image/jpeg" }));
    }, "image/jpeg", 0.88);
  }

  async function loadHistory() {
    setHistoryBusy(true);
    try {
      const rows = await window.uzhavamApi("/api/portal/diagnoses");
      setHistory(Array.isArray(rows) ? rows : []);
    } catch (cause) {
      setError(`${text.historyError} ${cause.message}`);
    } finally {
      setHistoryBusy(false);
    }
  }

  async function removeHistoryItem(id) {
    if (!window.confirm(text.deleteConfirm)) return;
    setError("");
    try {
      await window.uzhavamApi(`/api/portal/diagnoses/${id}`, { method: "DELETE" });
      setHistory(current => current.filter(item => item.id !== id));
      if (result?.id === id) setResult(null);
      setNotice(text.deleted);
    } catch (cause) {
      setError(`${text.historyError} ${cause.message}`);
    }
  }

  async function submit(event) {
    event.preventDefault();
    setError("");
    setNotice("");
    if (!cropName.trim()) {
      setError(text.needCrop);
      return;
    }
    if (!file) {
      setError(text.fileError);
      return;
    }

    const form = new FormData();
    form.append("image", file, file.name);
    form.append("cropName", cropName.trim());
    form.append("district", district.trim());
    form.append("language", language);
    setBusy(true);
    try {
      const saved = await window.uzhavamApi("/api/advisory/diagnose", { method: "POST", body: form });
      if (!saved?.id) throw new Error(text.noResult);
      setResult(saved);
      setNotice(text.saved);
      await loadHistory();
    } catch (cause) {
      setError(`${text.requestError} ${cause.message}`);
    } finally {
      setBusy(false);
    }
  }

  const labelLevel = value => text[String(value || "unknown").toLowerCase()] || value || text.unknown;

  return (
    <div>
      <header className="page-head">
        <div>
          <div className="eyebrow">{text.eyebrow}</div>
          <h1>{text.title}</h1>
          <p className="subhead">{text.intro}</p>
        </div>
      </header>
      {visionConfigured === false && <div className="note" role="status" style={{ marginBottom: 16 }}>{text.visionSetup}</div>}
      <div className="grid two-col">
        <article className="card">
          <div className="card-heading"><h2>{text.photoTitle}</h2><span className="status-pill">AI estimate</span></div>
          <form onSubmit={submit}>
            <div className="form-grid">
              <div className="field">
                <label htmlFor="reactDiagnosisCrop">{text.crop}</label>
                <input id="reactDiagnosisCrop" value={cropName} maxLength={120}
                  onChange={event => setCropName(event.target.value)} placeholder={text.cropPlaceholder} required />
              </div>
              <div className="field">
                <label htmlFor="reactDiagnosisDistrict">{text.district}</label>
                <input id="reactDiagnosisDistrict" value={district} maxLength={120}
                  onChange={event => setDistrict(event.target.value)} />
              </div>
              <div className="field">
                <label htmlFor="reactDiagnosisLanguage">{text.language}</label>
                <select id="reactDiagnosisLanguage" value={language} onChange={event => {
                  setLanguage(event.target.value);
                  window.uzhavamSetLanguage?.(event.target.value);
                }}>
                  {languages.map(item => <option key={item}>{item}</option>)}
                </select>
              </div>
            </div>
            <input ref={fileInput} type="file" accept="image/jpeg,image/png" capture="environment" hidden
              onChange={event => chooseFile(event.target.files?.[0])} />
            <div className="form-actions">
              <button className="button light" type="button" onClick={() => fileInput.current?.click()}>{text.choosePhoto}</button>
              {!cameraOpen
                ? <button className="button light" type="button" onClick={openCamera}>{text.openCamera}</button>
                : <>
                    <button className="button light" type="button" onClick={capturePhoto} disabled={!cameraReady}>{text.capture}</button>
                    <button className="button light" type="button" onClick={stopCamera}>{text.closeCamera}</button>
                  </>}
            </div>
            {cameraOpen && <video ref={video} className="plant-camera" autoPlay playsInline onLoadedMetadata={() => setCameraReady(true)} aria-label={text.openCamera} />}
            <canvas ref={canvas} hidden />
            {preview && <img className="plant-preview" src={preview} alt={text.photoTitle} />}
            <p className="subhead" style={{ marginTop: 12 }}>{text.photoTip}</p>
            <p className="task-meta" style={{ marginTop: 5 }}>{text.privacy}</p>
            {error && <p className="login-error" role="alert">{error}</p>}
            {notice && <p className="voice-progress" role="status">{notice}</p>}
            <div className="form-actions">
              <button className="button" type="submit" disabled={!file || busy}>
                {busy ? text.analyzing : text.analyze}
              </button>
            </div>
          </form>
        </article>
        {result ? (
          <article className="card">
            <div className="card-heading">
              <h2>{text.resultTitle}</h2>
              <span className="status-pill">{text.confidence}: {labelLevel(result.confidence)}</span>
            </div>
            <p><strong>{text.likelyIssue}: {result.likelyIssue}</strong></p>
            <p className="subhead">{text.cropFound}: {result.cropIdentified} · {text.severity}: {labelLevel(result.severity)}</p>
            <p className="plant-result" style={{ marginTop: 13 }}><strong>{text.observations}</strong><br />{result.observations}</p>
            <div className="resource-why plant-result"><strong>{text.recommendations}</strong><br />{result.recommendations}</div>
            <div className="note">{text.notConfirmed}</div>
            <div className="history-list" style={{ marginTop: 10 }}>
              {(result.sourceReferences || []).map((source, index) => (
                <div className="plant-history-item" key={`${index}-${source}`}><strong>{text.source} {index + 1}</strong><br />{source}</div>
              ))}
            </div>
          </article>
        ) : (
          <article className="card">
            <h2>{text.resultTitle}</h2>
            <p className="subhead" style={{ marginTop: 8 }}>{text.notConfirmed}</p>
          </article>
        )}
      </div>
      <article className="card" style={{ marginTop: 16 }}>
        <div className="card-heading">
          <div><h2>{text.historyTitle}</h2><p className="subhead">{text.historyTip}</p></div>
          <button className="text-link" type="button" onClick={loadHistory} disabled={historyBusy}>{text.refresh}</button>
        </div>
        {historyBusy ? <p className="empty">{text.loadingHistory}</p> : history.length === 0
          ? <p className="empty">{text.emptyHistory}</p>
          : history.map(item => (
              <div className="plant-history-item" key={item.id} style={{ display: "flex", alignItems: "start", justifyContent: "space-between", gap: 12 }}>
                <div>
                  <strong>{item.cropName} · {item.likelyIssue}</strong>
                  <div className="task-meta">{item.district} · {labelLevel(item.severity)} severity · {text.confidence}: {labelLevel(item.confidence)}</div>
                  <div className="plant-result">{item.observations}</div>
                  <div className="task-meta">{text.savedAt}: {new Date(item.createdAt).toLocaleString()}</div>
                </div>
                <button className="button danger small" type="button" onClick={() => removeHistoryItem(item.id)}>{text.delete}</button>
              </div>
            ))}
      </article>
    </div>
  );
}

const root = document.getElementById("plant-health-root");
if (root) createRoot(root).render(<PlantHealthApp />);
