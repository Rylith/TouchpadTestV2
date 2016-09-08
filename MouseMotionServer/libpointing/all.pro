# all.pro --
#
# Initial software
# Authors: Nicolas Roussel
# Copyright © Inria

TEMPLATE = subdirs

ALL_APPS = pointing apps tests
for(dir, ALL_APPS) {
    exists($$dir) {
        SUBDIRS += $$dir
    }
}
