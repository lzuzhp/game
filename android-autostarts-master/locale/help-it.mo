��          �      �       0     1  n  J  ;   �  �  �  i   �  r  "  W   �  �  �  [   �  �   �  +   �	  �  �	  �  d     =  �  L  9   �  �    p     �  y  V   &  �  }  V   -  �   �  3   V  �  �                                             
         	    faq1:0How does it work? faq1:1<p> Android allows applications to register for a variety of events supported by the system, to be started whenever such an event triggers. 

This allows applications to do important work in the background, but may sometimes also slow down the system. 

Autostarts helps you keep control over your phone by letting you know what goes on behind your back. </p> faq2:0Can I remove an application from a particular event? faq2:1Yes, but due to Android API limitations, it only works on rooted devices. <b>Please be careful when disabling stuff.</b> In particular note that the component in question may be responsible for other parts of the application's functionality than just the given event. As a result, you may experience unintended side effects. For the most part though, you can just give things a try, and enable the component again should the application break. faq3:0When I activate the "show changed only" filter, why do I see stuff that I never actually modified? faq3:1Whether a component has changed (and is thus displayed in bold) is determined by looking at it's default state versus it's current state. It is possible for a component to be disabled <i>by default</i> after installation, but being activated by the application itself once needed (for example after you use a specific feature or enable a setting the application). faq4:0Is it possible to know what a particular application does on a particular event? faq4:1No, not per se. However, if you select an entry, you will be able to see what the name of the <i>component</i> is that handles the event. Sometimes, the name gives you a hint. For example, an <i>AlarmInitReceiver</i> would likely initialize the alarms or notification that you have enabled in the application. Don't be surprised then if this application's alarms stop working if you disable the component. faq5:0I've disabled a component, but it seems to have it self enabled again after a while? faq5:1Yes, applications may enable or disable their own components. Some do this depending on settings, or what features are enabled. Unfortunately, there is no workaround for this. faq6:0My Market downloads stopped working. faq6:1This is usually not the fault of <i>Autostarts</i>, don't get ideas. However, there is one issue you might have run into. If you thought you weren't using Google Talk anyway and disabled it's <i>On Boot</i> event, this is likely to be the problem (it also occurs of you shutdown Google Talk using a task manager). Apparently, for some reason, if Google Talk is not running, market downloads do not work. Project-Id-Version: android-autostarts
Report-Msgid-Bugs-To: http://github.com/miracle2k/android-autostarts/issues
POT-Creation-Date: 2012-08-14 00:42+0200
PO-Revision-Date: 2012-08-14 00:46+0100
Last-Translator: 
Language-Team: Italian (http://www.transifex.net/projects/p/android-autostarts/team/it/)
Plural-Forms: nplurals=2; plural=((n == 1) ? 0 : 1)
MIME-Version: 1.0
Content-Type: text/plain; charset=utf-8
Content-Transfer-Encoding: 8bit
Generated-By: Babel 1.0dev
 Come funziona? <p> Android permette alle applicazioni di registrarsi per una serie di eventi sostenuti dal sistema, per essere avviati ogni volta che un evento del genere si verifica. 

Questo permette alle applicazioni di lavorare in background, ma talvolta può anche rallentare il sistema. 

Autostarts ti aiuta a mantenere il controllo sul tuo cellulare, consentendo di sapere cosa succede dietro. </p> Posso rimuovere un'applicazione da un evento particolare? Sì, ma a causa di limitazioni delle API Android, funziona solo su dispositivi con root. <b>Si prega di fare attenzione a disabilitare codesto.</b> In particolar nota che la componente in questione può essere responsabile di altre parti della funzionalità dell'applicazione non solo il dato evento. Di conseguenza, potrebbero verificarsi effetti collaterali indesiderati. Per la maggior parte, però si può semplicemente fare delle prove, e attivare dinuovo il componente dell'applicazione danneggiata. Quando ho attivato il filtro "mostra solo cambiati", perchè vedo cose che non ho mai effettivamente modificato? Se un componente è stato modificato (e quindi è visualizzato in grassetto) è determinato con riferimento al suo stato di default rispetto allo stato attuale. E' possibile che per un componente venga disabilitato <i>di default</i> dopo l'installazione, ma venga attivato dall'applicazione stessa una volta necessario (per esempio dopo aver utilizzato una caratteristica specifica o attivato un impostazione dell'applicazione). E 'possibile sapere che cosa fa una particolare applicazione su un particolare evento? No, non di per se, tuttavia se si seleziona una voce, si sarà in grado di vedere il nome del <i>componente</i> che gestisce l'evento. A volte, il nome che si dà è un suggerimento. Per esempio, <i>AlarmInitReceiver</i> avrebbe probabilmente iniziato l'allarme o la notifica che si è attivata nell' applicazione. Non sorprendetevi quindi se gli allarmi di questa applicazione smettono di funzionare se si disattiva il componente. Ho disattivato un componente, ma sembra avere lo stesso permesso di nuovo dopo un po'? Si, le applicazioni possono abilitare o disattivare i propri componenti. Alcune fanno questo a seconda delle impostazioni, o quali funzioni sono attivate. Sfortunatamente, non c'è alcuna soluzione per questo. I miei Market downloads hanno smesso di funzionare. Questo di solito non è colpa di <i>Autostarts</i>, non fatevi idee. Tuttavia, c'è un problema che potrebbe essere avvenuto. Se avete pensato che Google Talk non servisse è avete disattivato l'evento <i>Al Boot</i>, questo è probabilmente il problema (che si attiva anche alla chiusura di Google Talk  usando il task manager). A quanto pare, per qualche ragione, se Google Talk non è  avviato, i downloads dal market non funzionano. 