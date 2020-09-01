let application = new DynamicApplication(document.body)
application.registerInteractiveElementType('.dynamic-content',
    DynamicContentArea);
application.initializeInteractiveElements(application.element)

