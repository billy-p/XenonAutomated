package org.bill.xenonautomated.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.bill.xenonautomated.R;
import org.bill.xenonautomated.enums.ValidArguments;
import org.bill.xenonautomated.interfaces.ArgumentTypes;

import java.util.HashMap;

public class ArgumentsCreator extends HashMap<ValidArguments,MinMaxArhumentCreator>{

    public void initializeArgumentCreator(Context context) {
        addAllMappings(context);
    }
    private void addAllMappings(final Context context)
    {
        /* Initialize map */
        this.put(ValidArguments.INT,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Integer.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Integer.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 1;
            }
        }));
        this.put(ValidArguments.INTEGER,new MinMaxArhumentCreator(new ArgumentTypes() {
        @Override
        public Object getArgumentValue() {
            return Integer.MAX_VALUE;
        }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Integer.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 1;
            }
        }));
        this.put(ValidArguments.LONG,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Long.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Long.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 112L;
            }
        }));
        this.put(ValidArguments.LONG_CLASS,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Long.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Long.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 112L;
            }
        }));
        this.put(ValidArguments.SHORT,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Short.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Short.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 5;
            }
        }));
        this.put(ValidArguments.SHORT_CLASS,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Short.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Short.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 5;
            }
        }));
        this.put(ValidArguments.FLOAT,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Float.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Float.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 3.6f;
            }
        }));
        this.put(ValidArguments.FLOAT_CLASS,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Float.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Float.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 3.6f;
            }
        }));
        this.put(ValidArguments.DOUBLE,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Double.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Double.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 3.5;
            }
        }));
        this.put(ValidArguments.DOUBLE_CLASS,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Double.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Double.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 3.5;
            }
        }));
        this.put(ValidArguments.BOOLEAN,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return true;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return false;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return true;
            }
        }));
        this.put(ValidArguments.BOOLEAN_CLASS,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return true;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return false;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return true;
            }
        }));
        this.put(ValidArguments.BYTE,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Byte.MAX_VALUE;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return Byte.MIN_VALUE;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 0;
            }
        }));
        this.put(ValidArguments.CHAR,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return '\uffff';
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return '\u0000';
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return 's';
            }
        }));
        this.put(ValidArguments.CHAR_SEQUENCE,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return new String(new char[21474836]).replace("\0", "c");
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return "b";
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return "d";
            }
        }));
        this.put(ValidArguments.STRING,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return new String(new char[21474836]).replace("\0", "c");
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return "b";
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return "d";
            }
        }));
        this.put(ValidArguments.BITMAP,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return BitmapFactory.decodeResource( context.getResources(), R.drawable.spring_oak_medium);
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return BitmapFactory.decodeResource( context.getResources(), R.drawable.spring_oak_small);
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return BitmapFactory.decodeResource( context.getResources(), R.drawable.spring_oak_small);
            }
        }));
        this.put(ValidArguments.ICON,new MinMaxArhumentCreator(new ArgumentTypes() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public Object getArgumentValue() {
                return Icon.createWithResource(context, R.drawable.spring_oak_medium);
            }
        }, new ArgumentTypes() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public Object getArgumentValue() {
                return Icon.createWithResource(context, R.drawable.spring_oak_small);
            }
        },new ArgumentTypes() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public Object getArgumentValue() {
                return Icon.createWithResource(context, R.drawable.spring_oak_small);
            }
        }));
        this.put(ValidArguments.CONTEXT,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return context;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return context;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return context;
            }
        }));
        this.put(ValidArguments.RESOURCE_ID,new MinMaxArhumentCreator(new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return R.drawable.spring_oak_extralarge;
            }
        }, new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return R.drawable.spring_oak_small;
            }
        },new ArgumentTypes() {
            @Override
            public Object getArgumentValue() {
                return R.drawable.spring_oak_small;
            }
        }));
    }
}

