# myThreadAgent

## jattach使用说明

> Github：https://github.com/jattach/jattach/blob/master/README.md?plain=1

### Examples
#### Load native agent

    $ jattach <pid> load <.so-path> { true | false } [ options ]

Where `true` means that the path is absolute, `false` -- the path is relative.

`options` are passed to the agent.

#### Load Java agent

Java agents are loaded by the special built-in native agent named `instrument`,
which takes .jar path and its arguments as a single options string.

    $ jattach <pid> load instrument false "javaagent.jar=arguments"

#### List available jcmd commands

    $ jattach <pid> jcmd help -all

### Installation
#### Debian, Ubuntu

On Debian and Ubuntu, you can install `jattach` from the official repository:

    # apt install jattach

#### Alpine Linux

On Alpine Linux, you can install `jattach` package from the edge/community repository:

    # apk add --no-cache jattach --repository http://dl-cdn.alpinelinux.org/alpine/edge/community/

#### Archlinux

[jattach](https://aur.archlinux.org/packages/jattach/) package can be installed from [AUR](https://wiki.archlinux.org/index.php/Arch_User_Repository) using one of [AUR helpers](https://wiki.archlinux.org/index.php/AUR_helpers), e.g., `yay`:

    # yay -S jattach

#### FreeBSD

On FreeBSD, you can use the following command to install `jattach`:

    # pkg install jattach

### 自定义agent挂在示例
```
./jattach-mac 9755 load instrument false "/.../myThreadAgent.jar=5-60"
```

--------------

## 探针小结

### 获取不到类
通过类加载器中的classes属性获取类的方式，无法获取到Bootstrap启动类加载器加载的类，比如ThreadPoolExecutor；
