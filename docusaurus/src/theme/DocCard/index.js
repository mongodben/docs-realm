/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
import React from "react";
import Link from "@docusaurus/Link";
import { findFirstCategoryLink, useDocById } from "@docusaurus/theme-common";
import clsx from "clsx";
import styles from "./styles.module.css";
import isInternalUrl from "@docusaurus/isInternalUrl";
import { translate } from "@docusaurus/Translate";
import FlutterIcon from "../../icons/flutter.svg";
import AndroidIcon from "../../icons/android_sdk.svg";
import KotlinIcon from "../../icons/kotlin_sdk.svg";

function CardContainer({ href, children }) {
  console.log(children);
  const className = clsx(
    "card margin-bottom--lg padding--lg",
    styles.cardContainer,
    href && styles.cardContainerLink
  );
  return href ? (
    <Link href={href} className={className}>
      {children}
    </Link>
  ) : (
    <div className={className}>{children}</div>
  );
}

function CardLayout({ href, icon, title, description }) {
  console.log(icon);
  if (icon.endsWith("Icon")) {
    switch (icon) {
      case "FlutterIcon":
        icon = <FlutterIcon style={{ height: 20, width: 20 }} />;
        break;
      case "AndroidIcon":
        icon = <AndroidIcon style={{ height: 20, width: 20 }} />;
        break;
      case "KotlinIcon":
        icon = <KotlinIcon style={{ height: 20, width: 20 }} />;
        break;
      default:
        break;
    }
    // icon = <img src={icon} />;
  }
  return (
    <CardContainer href={href}>
      <h2 className={clsx("text--truncate", styles.cardTitle)} title={title}>
        {icon} {title}
      </h2>
      <div
        className={clsx("text--truncate", styles.cardDescription)}
        title={description}
      >
        {description}
      </div>
    </CardContainer>
  );
}

function CardCategory({ item }) {
  const href = findFirstCategoryLink(item);
  return (
    <CardLayout
      href={href}
      icon={item.customProps?.svg_icon || "🗃️"}
      title={item.label}
      description={translate(
        {
          message: "{count} items",
          id: "theme.docs.DocCard.categoryDescription",
          description:
            "The default description for a category card in the generated index about how many items this category includes",
        },
        {
          count: item.items.length,
        }
      )}
    />
  );
}

function CardLink({ item }) {
  console.log(item);
  const icon =
    item.customProps?.svg_icon || (isInternalUrl(item.href) ? "📄️" : "🔗");
  const doc = useDocById(item.docId ?? undefined);
  return (
    <CardLayout
      href={item.href}
      icon={icon}
      title={item.label}
      description={doc?.description || item.customProps?.description}
    />
  );
}

export default function DocCard({ item }) {
  switch (item.type) {
    case "link":
      return <CardLink item={item} />;

    case "category":
      return <CardCategory item={item} />;

    default:
      throw new Error(`unknown item type ${JSON.stringify(item)}`);
  }
}
