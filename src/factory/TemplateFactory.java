package factory;

import model.StripTemplate;
import template.TemplateSquare;
import template.TemplateVertical;
import template.TemplateClassicStrip;
import template.TemplateHorizontal;
import template.TemplateFeatured;
import template.TemplateFourCorners;

public class TemplateFactory {

    public StripTemplate createTemplate(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            return null;
        }

        if (templateId.equalsIgnoreCase("TPL-V")) {
            return new TemplateVertical(4); // Default
        } else if (templateId.equalsIgnoreCase("TPL-V-2")) {
            return new TemplateVertical(2);
        } else if (templateId.equalsIgnoreCase("TPL-V-3")) {
            return new TemplateVertical(3);
        } else if (templateId.equalsIgnoreCase("TPL-V-4")) {
            return new TemplateVertical(4);

        } else if (templateId.equalsIgnoreCase("TPL-H")) {
            return new TemplateHorizontal(4); // Default
        } else if (templateId.equalsIgnoreCase("TPL-H-2")) {
            return new TemplateHorizontal(2);
        } else if (templateId.equalsIgnoreCase("TPL-H-3")) {
            return new TemplateHorizontal(3);
        } else if (templateId.equalsIgnoreCase("TPL-H-4")) {
            return new TemplateHorizontal(4);

        } else if (templateId.equalsIgnoreCase("TPL-SQ")) {
            return new TemplateSquare();
        } else if (templateId.equalsIgnoreCase("TPL-C")) {
            return new TemplateClassicStrip();
        } else if (templateId.equalsIgnoreCase("TPL-F")) {
            return new TemplateFeatured();
        } else if (templateId.equalsIgnoreCase("TPL-COR")) {
            return new TemplateFourCorners();
        }

        return null;
    }
}