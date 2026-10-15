import argparse, sys
from GT2SaveEditorForm import GT2SaveEditorForm

if __name__ == "__main__":
    args = sys.argv
    GT2SaveEditorForm(args[1] if len(args) > 1 else None).mainloop()