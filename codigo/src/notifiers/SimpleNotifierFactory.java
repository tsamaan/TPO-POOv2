package notifiers;

import interfaces.INotifier;

public class SimpleNotifierFactory extends NotifierFactory {

    @Override
    public INotifier createEmailNotifier() {
        return new EmailNotifier();
    }
}
